package com.konak.goodgames.service.impl;

import com.konak.goodgames.domain.dto.UserDto;
import com.konak.goodgames.domain.dto.UserInfoDto;
import com.konak.goodgames.domain.model.CustomUserDetails;
import com.konak.goodgames.domain.model.User;
import com.konak.goodgames.exception.BadRequestException;
import com.konak.goodgames.exception.ConflictException;
import com.konak.goodgames.exception.NotFoundException;
import com.konak.goodgames.repository.UserRepository;
import com.konak.goodgames.service.UserService;
import com.konak.goodgames.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final AuthenticationManager authenticationManager;

  private final JwtTokenUtil jwtTokenUtil;

  private final ModelMapperService modelMapperService;

  private final UserRepository userRepository;

  @Override
  public void register(UserDto userDto) {
    Optional<User> userByEmail = userRepository.findByEmail(userDto.getEmail());
    if (userByEmail.isPresent()) {
      throw new ConflictException("User already exists!");
    }
    User user = modelMapperService.map(userDto, User.class);
    userRepository.save(user);
  }

  @Override
  public void login(UserDto userDto, HttpServletResponse response) {
    try {
      Authentication authenticate =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));

      CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();

      response.addHeader(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateToken(userDetails));
      response.setStatus(HttpStatus.OK.value());
    } catch (BadCredentialsException ex) {
      throw new BadRequestException("Invalid credentials!");
    }
  }

  @Override
  public UserInfoDto getUserInfo() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    User user = userDetails.user();
    return modelMapperService.map(user, UserInfoDto.class);
  }
}
