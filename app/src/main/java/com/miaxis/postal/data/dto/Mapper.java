package com.miaxis.postal.data.dto;

import com.miaxis.postal.data.exception.MyException;

public interface Mapper<T>{
   T transform() throws MyException;
}