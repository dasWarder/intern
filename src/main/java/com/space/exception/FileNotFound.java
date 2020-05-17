package com.space.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.FileNotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFound extends RuntimeException {

}
