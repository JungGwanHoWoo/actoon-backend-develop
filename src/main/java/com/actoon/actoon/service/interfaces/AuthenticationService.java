package com.actoon.actoon.service.interfaces;

import com.actoon.actoon.dto.BasicResponse;
import com.actoon.actoon.dto.JwtAuthenticationResponse.*;
import com.actoon.actoon.dto.UserDto.*;

import javax.naming.NoPermissionException;

public interface AuthenticationService {
    BasicResponse signup(SignUpDto request) throws NoPermissionException;
    IssueJwtAuthentication signin(SignInDto request);
}