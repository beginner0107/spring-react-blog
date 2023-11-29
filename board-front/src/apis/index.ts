import { SignInRequestDto, SignUpRequestDto } from "./request/auth";

const DOMAIN = "http://localhost:4000";

const API_DOMAIN = `${DOMAIN}/api/v1`;

const SIGN_IN_URL = () => `${API_DOMAIN}/auth/sign-in`;
const SIGN_UP_URL = () => `${API_DOMAIN}/auth-sign-up`;

export const signInRequest = (requestBody: SignInRequestDto) => {};

export const signUnRequest = (requestBody: SignUpRequestDto) => {};
