import React, {
  useState,
  KeyboardEvent,
  useRef,
  ChangeEvent,
  useEffect,
} from "react";
import "./style.css";
import InputBox from "components/InputBox";
import SignInRequestDto from "../../apis/request/auth/sign-in.request.dto";
import { SignInResponseDto } from "apis/response/auth";
import { useCookies } from "react-cookie";
import { MAIN_PATH } from "constant";
import { useNavigate } from "react-router-dom";
import { signInRequest, signUpRequest } from "apis";
import { Address, useDaumPostcodePopup } from "react-daum-postcode";
import { SignUpRequestDto } from "apis/request/auth";
import ResponseDto from "apis/response/response.dto";

//          component: 인증 화면 컴포넌트         //
export default function Authentication() {
  //          state: 화면 상태          //
  const [view, setView] = useState<"sign-in" | "sign-up">("sign-in");
  //          state: 쿠키 상태          //
  const [cookie, setCookie] = useCookies();

  //          function: 네비게이트 함수          //
  const navigator = useNavigate();

  //          component: sign in card 컴포넌트          //
  const SignInCard = () => {
    //          state: 이메일 요소 참조 상태          //
    const emailRef = useRef<HTMLInputElement | null>(null);
    //          state: 패스워드 요소 참조 상태          //
    const passwordRef = useRef<HTMLInputElement | null>(null);

    //          state: 이메일 상태          //
    const [email, setEmail] = useState<string>("");
    //          state: 패스워드 상태          //
    const [password, setPassword] = useState<string>("");
    //          state: 패스워드 타입 상태          //
    const [passwordType, setPasswordType] = useState<"text" | "password">(
      "password"
    );
    //          state: 패스워드 아이콘 상태          //
    const [passwordButtonIcon, setPasswordButtonIcon] = useState<
      "eye-light-off-icon" | "eye-light-on-icon"
    >("eye-light-off-icon");
    //          state: 패스워드 타입 상태          //
    const [error, setError] = useState<boolean>(false);

    //           state: 이메일 에러 상태           //
    const [isEmailError, setEmailError] = useState<boolean>(false);
    //           state: 패스워드 에러 상태           //
    const [isPasswordError, setPasswordError] = useState<boolean>(false);

    //          state: 이메일 에러 메시지 상태           //
    const [emailErrorMessage, setEmailErrorMessage] = useState<string>("");
    //          state: 패스워드 에러 메시지 상태           //
    const [passwordErrorMessage, setPasswordErrorMessage] =
      useState<string>("");

    //          function: sign in response 처리 함수          //
    const signInResponse = (responseBody: SignInResponseDto | null | 401) => {
      if (!responseBody) {
        alert("네트워크 이상입니다.");
        return;
      }
      if (responseBody === 401) {
        setError(true);
        return;
      }

      const { token, expirationTime } = responseBody; // 강제 형변환(as SignInResponseDto) 제거
      if (token === "" || expirationTime === 0) return;
      const now = new Date().getTime();
      const expires = new Date(now + expirationTime * 1000);

      setCookie("accessToken", token, { expires, path: MAIN_PATH() });
      navigator(MAIN_PATH());
    };

    //           event handler: 이메일 변경 이벤트 처리           //
    const onEmailChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
      setError(false);
      const { value } = event.target;
      setEmail(value);
    };

    //           event handler: 비밀번호 변경 이벤트 처리           //
    const onPasswordChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
      setError(false);
      const { value } = event.target;
      setPassword(value);
    };

    //           event handler: 로그인 버튼 클릭 이벤트 처리          //
    const onSignInButtonClickHandler = () => {
      const emailPattern = /^[a-zA-Z0-9]*@([-.]?[a-zA-Z0-9])*\.[a-zA-Z]{2,4}$/;
      const isEmailPattern = emailPattern.test(email);
      if (!isEmailPattern) {
        setEmailError(true);
        setEmailErrorMessage("이메일 주소 형식이 맞지 않습니다.");
      }
      const passwordPattern = /^(?=.*?[A-Za-z])(?=.*?\d).{8,20}$/;
      const isPasswordPattern = passwordPattern.test(password.trim());

      if (!isPasswordPattern) {
        setPasswordError(true);
        setPasswordErrorMessage(
          "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다."
        );
      }

      if (!isEmailPattern || !isPasswordPattern) return;

      const requestBody: SignInRequestDto = { email, password };
      signInRequest(requestBody).then(signInResponse);
    };
    //           event handler: 회원가입 버튼 링크 이벤트 처리          //
    const onSignUpLinkClickHandler = () => {
      setView("sign-up");
    };
    //           event handler: 패스워드 버튼 클릭 이벤트 처리          //
    const onPasswordButtonClickHandler = () => {
      if (passwordType === "text") {
        setPasswordType("password");
        setPasswordButtonIcon("eye-light-off-icon");
      } else {
        setPasswordType("text");
        setPasswordButtonIcon("eye-light-on-icon");
      }
    };

    //          event handler: 이메일 인풋 키 다운 이벤트 처리          //
    const onEmailKeyDownHandler = (event: KeyboardEvent<HTMLInputElement>) => {
      if (event.key !== "Enter") return;
      if (!passwordRef.current) return;
      passwordRef.current.focus();
    };

    //          event handler: 패스워드 인풋 키 다운 이벤트 처리          //
    const onPasswordKeyDownHandler = (
      event: KeyboardEvent<HTMLInputElement>
    ) => {
      if (event.key !== "Enter") return;
      onSignInButtonClickHandler();
    };

    //        render: sign in card 컴포넌트 렌더링          //
    return (
      <div className="auth-card">
        <div className="auth-card-box">
          <div className="auth-card-top">
            <div className="auth-card-title-box">
              <div className="auth-card-title">{"로그인"}</div>
            </div>
            <InputBox
              ref={emailRef}
              label="이메일 주소"
              type="text"
              placeholder="이메일 주소를 입력해주세요."
              error={error}
              value={email}
              onChange={onEmailChangeHandler}
              onKeyDown={onEmailKeyDownHandler}
            />
            <InputBox
              ref={passwordRef}
              label="패스워드"
              type={passwordType}
              placeholder="비밀번호를 입력해주세요."
              error={error}
              value={password}
              onChange={onPasswordChangeHandler}
              icon={passwordButtonIcon}
              onButtonClick={onPasswordButtonClickHandler}
              onKeyDown={onPasswordKeyDownHandler}
            />
          </div>
          <div className="auth-card-bottom">
            {error && (
              <div className="auth-sign-in-error-box">
                <div className="auth-sign-in-error-message">
                  {
                    "이메일 주소 또는 비밀번호를 잘못 입력했습니다.\n입력하신 내용을 다시 확인해주세요."
                  }
                </div>
              </div>
            )}
            <div
              className="black-large-full-button"
              onClick={onSignInButtonClickHandler}
            >
              {"로그인"}
            </div>
            <div className="auth-description-box">
              <div className="auth-description">
                {"신규 사용자이신가요?"}
                <span
                  className="auth-description-link"
                  onClick={onSignUpLinkClickHandler}
                >
                  {"  회원가입"}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  };
  //          component: sign up card 컴포넌트          //
  const SignUpCard = () => {
    //          state: 이메일 요소 참조 상태           //
    const emailRef = useRef<HTMLInputElement | null>(null);
    //          state: 패스워드 요소 참조 상태           //
    const passwordRef = useRef<HTMLInputElement | null>(null);
    //          state: 패스워드 확인 요소 참조 상태           //
    const passwordCheckRef = useRef<HTMLInputElement | null>(null);
    //          state: 닉네임 요소 참조 상태           //
    const nicknameRef = useRef<HTMLInputElement | null>(null);
    //          state: 핸드폰 번호 요소 참조 상태           //
    const telNumberRef = useRef<HTMLInputElement | null>(null);
    //          state: 주소 요소 참조 상태           //
    const addressRef = useRef<HTMLInputElement | null>(null);
    //          state: 주소 요소 참조 상태           //
    const addressDetailRef = useRef<HTMLInputElement | null>(null);

    //           state: 페이지 번호 상태           //
    const [page, setPage] = useState<1 | 2>(1);
    //           state: 이메일 상태           //
    const [email, setEmail] = useState<string>("");
    //           state: 패스워드 상태           //
    const [password, setPassword] = useState<string>("");
    //           state: 패스워드 확인 상태           //
    const [passwordCheck, setPasswordCheck] = useState<string>("");
    //           state: 닉네임 상태           //
    const [nickname, setNickaname] = useState<string>("");
    //           state: 핸드폰 번호 상태           //
    const [telNumber, setTelNumber] = useState<string>("");
    //           state: 주소 상태           //
    const [address, setAddress] = useState<string>("");
    //           state: 상세 주소 상태           //
    const [addressDetail, setAddressDetail] = useState<string>("");

    //          state: 패스워드 타입 상태          //
    const [passwordType, setPasswordType] = useState<"text" | "password">(
      "password"
    );
    //          state: 패스워드 체크 타입 상태          //
    const [passwordCheckType, setPasswordCheckType] = useState<
      "text" | "password"
    >("password");
    //           state: 이메일 에러 상태           //
    const [isEmailError, setEmailError] = useState<boolean>(false);
    //           state: 패스워드 에러 상태           //
    const [isPasswordError, setPasswordError] = useState<boolean>(false);
    //           state: 패스워드 확인 에러 상태           //
    const [isPasswordCheckError, setPasswordCheckError] =
      useState<boolean>(false);
    //           state: 닉네임 에러 상태           //
    const [isNicknameError, setNicknameError] = useState<boolean>(false);
    //           state: 핸드폰 번호 에러 상태           //
    const [isTelNumberError, setTelNumberError] = useState<boolean>(false);
    //           state: 주소 에러 상태           //
    const [isAddressError, setAddressError] = useState<boolean>(false);

    //          state: 이메일 에러 메시지 상태           //
    const [emailErrorMessage, setEmailErrorMessage] = useState<string>("");
    //          state: 패스워드 에러 메시지 상태           //
    const [passwordErrorMessage, setPasswordErrorMessage] =
      useState<string>("");
    //          state: 패스워드 확인 에러 메시지 상태           //
    const [passwordCheckErrorMessage, setPasswordCheckErrorMessage] =
      useState<string>("");
    //          state: 닉네임 에러 메시지 상태           //
    const [nicknameErrorMessage, setNicknameErrorMessage] =
      useState<string>("");
    //          state: 핸드폰 번호 에러 메시지 상태           //
    const [telNumberErrorMessage, setTelNumberErrorMessage] =
      useState<string>("");
    //          state: 주소 에러 메시지 상태           //
    const [addressErrorMessage, setAddressErrorMessage] = useState<string>("");

    //          state: 패스워드 아이콘 상태          //
    const [passwordButtonIcon, setPasswordButtonIcon] = useState<
      "eye-light-off-icon" | "eye-light-on-icon"
    >("eye-light-off-icon");
    //          state: 패스워드 확인 아이콘 상태          //
    const [passwordCheckButtonIcon, setPasswordCheckButtonIcon] = useState<
      "eye-light-off-icon" | "eye-light-on-icon"
    >("eye-light-off-icon");

    //          function: 다음 주소 검색 팝업 오픈 함수           //
    const open = useDaumPostcodePopup();

    //          function: sign up response 처리 함수           //
    const signUpResponse = (responseBody: ResponseDto | null) => {
      if (!responseBody) {
        alert("네트워크 이상입니다.");
        return;
      }
      if (responseBody.field === "email" && responseBody.status === 409) {
        setEmailError(true);
        setEmailErrorMessage(responseBody.message);
      }

      if (responseBody.field === "nickname" && responseBody.status === 409) {
        setNicknameError(true);
        setNicknameErrorMessage(responseBody.message);
      }
      if (responseBody.field === "telNumber" && responseBody.status === 409) {
        setTelNumberError(true);
        setTelNumberErrorMessage(responseBody.message);
      }
      if (
        responseBody.field === "email" ||
        (responseBody.field == "password" && responseBody.status === 400)
      ) {
        alert(responseBody.message);
      }
      if (responseBody.status == 200) {
        setView("sign-in");
      } else {
        return;
      }
    };

    //           event handler: 이메일 변경 이벤트 처리           //
    const onEmailChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
      const { value } = event.target;
      setEmail(value);
      setEmailError(false);
      setEmailErrorMessage("");
    };
    //           event handler: 패스워드 변경 이벤트 처리           //
    const onPasswordChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
      const { value } = event.target;
      setPassword(value);
      setPasswordError(false);
      setPasswordErrorMessage("");
    };
    //           event handler: 패스워드 확인 변경 이벤트 처리           //
    const onPasswordCheckChangeHandler = (
      event: ChangeEvent<HTMLInputElement>
    ) => {
      const { value } = event.target;
      setPasswordCheck(value);
      setPasswordCheckError(false);
      setPasswordCheckErrorMessage("");
    };
    //           event handler: 닉네임 변경 이벤트 처리           //
    const onNicknameChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
      const { value } = event.target;
      setNickaname(value);
      setNicknameError(false);
      setNicknameErrorMessage("");
    };
    //           event handler: 핸드폰 번호 변경 이벤트 처리           //
    const onTelNumberChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
      const { value } = event.target;
      setTelNumber(value);
      setTelNumberError(false);
      setTelNumberErrorMessage("");
    };
    //           event handler: 주소 변경 이벤트 처리           //
    const onAddressChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
      const { value } = event.target;
      setAddress(value);
      setAddressError(false);
      setAddressErrorMessage("");
    };
    //           event handler: 상세 주소 변경 이벤트 처리           //
    const onAddressDetailChangeHandler = (
      event: ChangeEvent<HTMLInputElement>
    ) => {
      const { value } = event.target;
      setAddressDetail(value);
    };

    //           event handler: 패스워드 버튼 클릭 이벤트 처리          //
    const onPasswordButtonClickHandler = () => {
      if (passwordType === "text") {
        setPasswordType("password");
        setPasswordButtonIcon("eye-light-off-icon");
      } else {
        setPasswordType("text");
        setPasswordButtonIcon("eye-light-on-icon");
      }
    };

    //           event handler: 패스워드 체크 버튼 클릭 이벤트 처리          //
    const onPasswordCheckButtonClickHandler = () => {
      if (passwordCheckType === "text") {
        setPasswordCheckType("password");
        setPasswordCheckButtonIcon("eye-light-off-icon");
      } else {
        setPasswordCheckType("text");
        setPasswordCheckButtonIcon("eye-light-on-icon");
      }
    };

    //          event handler: 주소 버튼 클릭 이벤트 처리          //
    const onAddressButtonClickHandler = () => {
      open({ onComplete });
    };

    //          event handler: 이메일 인풋 키 다운 이벤트 처리          //
    const onEmailKeyDownHandler = (event: KeyboardEvent<HTMLInputElement>) => {
      if (event.key !== "Enter") return;
      if (!passwordRef.current) return;
      passwordRef.current.focus();
    };

    //          event handler: 패스워드 인풋 키 다운 이벤트 처리          //
    const onPasswordKeyDownHandler = (
      event: KeyboardEvent<HTMLInputElement>
    ) => {
      if (event.key !== "Enter") return;
      if (!passwordCheckRef.current) return;
      passwordCheckRef.current.focus();
    };
    //          event handler: 패스워드 확인 인풋 키 다운 이벤트 처리          //
    const onPasswordCheckKeyDownHandler = (
      event: KeyboardEvent<HTMLInputElement>
    ) => {
      if (event.key !== "Enter") return;
      if (!nicknameRef.current) return;
      onNextButtonClickHandler();
      nicknameRef.current.focus();
    };
    //          event handler: 닉네임 키 다운 이벤트 처리          //
    const onNicknameKeyDownHandler = (
      event: KeyboardEvent<HTMLInputElement>
    ) => {
      if (event.key !== "Enter") return;
      if (!telNumberRef.current) return;
      telNumberRef.current.focus();
    };
    //          event handler: 핸드폰 번호 키 다운 이벤트 처리          //
    const onTelNumberKeyDownHandler = (
      event: KeyboardEvent<HTMLInputElement>
    ) => {
      if (event.key !== "Enter") return;
      onAddressButtonClickHandler();
    };
    //          event handler: 주소 키 다운 이벤트 처리          //
    const onAddressKeyDownHandler = (
      event: KeyboardEvent<HTMLInputElement>
    ) => {
      if (event.key !== "Enter") return;
      if (!addressDetailRef.current) return;
      addressDetailRef.current.focus();
      onSignUpButtonClickHandler();
    };
    //          event handler: 상세 주소 키 다운 이벤트 처리          //
    const onAddressDetailKeyDownHandler = (
      event: KeyboardEvent<HTMLInputElement>
    ) => {
      if (event.key !== "Enter") return;
    };

    //           event handler: 다음 주소 검색 완료 이벤트 처리          //
    const onComplete = (data: Address) => {
      const { address } = data;
      setAddress(address);
      if (!addressDetailRef.current) return;
      addressDetailRef.current.focus();
    };

    //           event handler: 다음 단계 버튼 링크 이벤트 처리          //
    const onNextButtonClickHandler = () => {
      const emailPattern = /^[a-zA-Z0-9]*@([-.]?[a-zA-Z0-9])*\.[a-zA-Z]{2,4}$/;
      const isEmailPattern = emailPattern.test(email);
      if (!isEmailPattern) {
        setEmailError(true);
        setEmailErrorMessage("이메일 주소 형식이 맞지 않습니다.");
      }
      const passwordPattern = /^(?=.*?[A-Za-z])(?=.*?\d).{8,20}$/;
      const isPasswordPattern = passwordPattern.test(password.trim());

      if (!isPasswordPattern) {
        setPasswordError(true);
        setPasswordErrorMessage(
          "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다."
        );
      }

      const isEqualPassword = password === passwordCheck;
      if (!isEqualPassword) {
        setPasswordCheckError(true);
        setPasswordCheckErrorMessage("비밀번호가 일치하지 않습니다.");
      }

      if (!isEmailPattern || !isPasswordPattern || !isEqualPassword) return;
      setPage(2);
    };
    //          event handler: 회원가입 버튼 클릭 이벤트 처리          //
    const onSignUpButtonClickHandler = () => {
      const emailPattern = /^[a-zA-Z0-9]*@([-.]?[a-zA-Z0-9])*\.[a-zA-Z]{2,4}$/;
      const isEmailPattern = emailPattern.test(email);
      if (!isEmailPattern) {
        setEmailError(true);
        setEmailErrorMessage("이메일 주소 형식이 맞지 않습니다.");
      }
      const passwordPattern = /^(?=.*?[A-Za-z])(?=.*?\d).{8,20}$/;
      const isPasswordPattern = passwordPattern.test(password.trim());

      if (!isPasswordPattern) {
        setPasswordError(true);
        setPasswordErrorMessage(
          "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다."
        );
      }

      const isEqualPassword = password === passwordCheck;
      if (!isEqualPassword) {
        setPasswordCheckError(true);
        setPasswordCheckErrorMessage("비밀번호가 일치하지 않습니다.");
      }

      if (!isEmailPattern || !isPasswordPattern || !isEqualPassword) {
        setPage(1);
        return;
      }

      const hasNickname = nickname.trim().length > 0;
      if (!hasNickname) {
        setNicknameError(true);
        setNicknameErrorMessage("닉네임을 입력해주세요.");
      }

      const telNumberPattern = /[0-9]{11,13}$/;
      const isTelNumberPattern = telNumberPattern.test(telNumber);
      if (!isTelNumberPattern) {
        setTelNumberError(true);
        setTelNumberErrorMessage("숫자만 입력해주세요.");
      }

      const hasAddress = address.trim().length > 0;
      if (!hasAddress) {
        setAddressError(true);
        setAddressErrorMessage("주소를 입력해주세요.");
      }

      if (!hasNickname || !isTelNumberPattern) return;

      const requestBody: SignUpRequestDto = {
        email,
        password,
        nickname,
        telNumber,
        address,
        addressDetail,
      };

      signUpRequest(requestBody).then(signUpResponse);
    };

    //          event handler: 로그인 링크 클릭 이벤트 처리          //
    const onSignInLinkClickHandler = () => {
      setView("sign-in");
    };

    //          effect: 페이지가 변경될 때 마다 실행될 함수          //
    useEffect(() => {
      if (page === 2) {
        if (!nicknameRef.current) return;
        nicknameRef.current.focus();
      }
    }, [page]);

    //        render: sign up card 컴포넌트 렌더링          //
    return (
      <div className="auth-card">
        <div className="auth-card-box">
          <div className="auth-card-top">
            <div className="auth-card-title-box">
              <div className="auth-card-title">{"회원가입"}</div>
              <div className="auth-card-page">{`${page}/2`}</div>
            </div>
            {page === 1 && (
              <>
                <InputBox
                  ref={emailRef}
                  label="이메일 주소*"
                  type="text"
                  placeholder="이메일 주소를 입력해주세요."
                  value={email}
                  onChange={onEmailChangeHandler}
                  error={isEmailError}
                  message={emailErrorMessage}
                  onKeyDown={onEmailKeyDownHandler}
                />
                <InputBox
                  ref={passwordRef}
                  label="비밀번호*"
                  type={passwordType}
                  placeholder="비밀번호는 8 ~ 20자여야 하고 영어, 숫자가 포함되어야 합니다."
                  value={password}
                  onChange={onPasswordChangeHandler}
                  error={isPasswordError}
                  message={passwordErrorMessage}
                  icon={passwordButtonIcon}
                  onButtonClick={onPasswordButtonClickHandler}
                  onKeyDown={onPasswordKeyDownHandler}
                />
                <InputBox
                  ref={passwordCheckRef}
                  label="비밀번호 확인*"
                  type={passwordCheckType}
                  placeholder="비밀번호를 다시 입력해주세요."
                  value={passwordCheck}
                  onChange={onPasswordCheckChangeHandler}
                  error={isPasswordCheckError}
                  message={passwordCheckErrorMessage}
                  icon={passwordCheckButtonIcon}
                  onButtonClick={onPasswordCheckButtonClickHandler}
                  onKeyDown={onPasswordCheckKeyDownHandler}
                />
              </>
            )}
            {page === 2 && (
              <>
                <InputBox
                  ref={nicknameRef}
                  label="닉네임*"
                  type="text"
                  placeholder="닉네임을 입력해주세요."
                  value={nickname}
                  onChange={onNicknameChangeHandler}
                  error={isNicknameError}
                  message={nicknameErrorMessage}
                  onKeyDown={onNicknameKeyDownHandler}
                />
                <InputBox
                  ref={telNumberRef}
                  label="핸드폰 번호*"
                  type="text"
                  placeholder="핸드폰 번호를 입력해주세요."
                  value={telNumber}
                  onChange={onTelNumberChangeHandler}
                  error={isTelNumberError}
                  message={telNumberErrorMessage}
                  onKeyDown={onTelNumberKeyDownHandler}
                />
                <InputBox
                  ref={addressRef}
                  label="주소*"
                  type="text"
                  placeholder="우편번호 찾기."
                  value={address}
                  onChange={onAddressChangeHandler}
                  error={isAddressError}
                  message={addressErrorMessage}
                  icon="expand-right-light-icon"
                  onButtonClick={onAddressButtonClickHandler}
                  onKeyDown={onAddressKeyDownHandler}
                />
                <InputBox
                  ref={addressDetailRef}
                  label="상세 주소"
                  type="text"
                  placeholder="상세 주소를 입력해주세요."
                  value={addressDetail}
                  onChange={onAddressDetailChangeHandler}
                  error={false}
                  onKeyDown={onAddressDetailKeyDownHandler}
                />
              </>
            )}
          </div>
          <div className="auth-card-bottom">
            {page === 1 && (
              <div
                className="black-large-full-button"
                onClick={onNextButtonClickHandler}
              >
                {"다음 단계"}
              </div>
            )}
            {page === 2 && (
              <>
                <div
                  className="black-large-full-button"
                  onClick={onSignUpButtonClickHandler}
                >
                  {"회원가입"}
                </div>
              </>
            )}
            <div className="auth-description-box">
              <div className="auth-description">
                {"이미 계정이 있으신가요?"}
                <span
                  className="auth-description-link"
                  onClick={onSignInLinkClickHandler}
                >
                  {"로그인"}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  };

  //        render: 인증 화면 컴포넌트 렌더링          //
  return (
    <div id="auth-wrapper">
      <div className="auth-conatiner">
        <div className="auth-jumbotron-box">
          <div className="auth-jumbotron-contents">
            <div className="auth-logo-icon"></div>
            <div className="auth-jumbotron-text-box">
              <div className="auth-jumbotron-text">{"환영합니다."}</div>
              <div className="auth-jumbotron-text">{"ZOOS BOARD 입니다."}</div>
            </div>
          </div>
        </div>
        {view === "sign-in" && <SignInCard />}
        {view === "sign-up" && <SignUpCard />}
      </div>
    </div>
  );
}
