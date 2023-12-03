import axios, { AxiosError, AxiosResponse } from "axios";
import { SignInRequestDto, SignUpRequestDto } from "./request/auth";
import { ResponseDto } from "./response";
import SignInResponseDto from "./response/auth/sign-in.response.dto";
import GetSignInUserResponseDto from "./response/user/get-sign-in-user.response.dto";
import { response } from "express";

const DOMAIN = "http://localhost:8084";

const API_DOMAIN = `${DOMAIN}/api/v1`;

const authorization = (accessToken: string) => {
  return {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  };
};

const SIGN_IN_URL = () => `${API_DOMAIN}/auth/sign-in`;
const SIGN_UP_URL = () => `${API_DOMAIN}/auth/sign-up`;

export const signInRequest = async (requestBody: SignInRequestDto) => {
  const result = await axios
    .post(SIGN_IN_URL(), requestBody)
    .then((response) => {
      const responseBody: SignInResponseDto = response.data;
      return responseBody;
    })
    .catch((error) => {
      if (error.response.status === 401) return 401;
      if (error.response.status === 400) return 400;
      if (error.response.status === 404) return 404;
      return null;
    });
  return result;
};

export const signUpRequest = async (requestBody: SignUpRequestDto) => {
  try {
    const result: AxiosResponse = await axios.post(SIGN_UP_URL(), requestBody);
    return {
      status: result.status,
      field: "",
      message: "",
    };
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError;
      const errorMessage: string = (
        axiosError.response?.data as { message: string }
      )?.message;
      const match = errorMessage.match(/\[([^\]]+)\] ([^:]+): (.+)/);
      let field = "";
      let errorMessageText = "";
      if (match) {
        field = match[2].trim();
        errorMessageText = match[3].trim();
      }
      const responseDto: ResponseDto = {
        status: axiosError.response?.status || 500,
        field: field,
        message: errorMessageText,
      };
      return responseDto;
    }
  }
  return null;
};

const GET_SIGN_IN_USER_URL = () => `${API_DOMAIN}/user`;

export const getSignInUserRequest = async (accessToken: string) => {
  const result = await axios
    .get(GET_SIGN_IN_USER_URL(), authorization(accessToken))
    .then((response) => {
      const responseBody: GetSignInUserResponseDto = response.data;
      return responseBody;
    })
    .catch((error) => {
      return null;
    });
  return result;
};
