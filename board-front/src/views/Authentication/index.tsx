import React, { useState } from "react";
import "./style.css";

//          component: 인증 화면 컴포넌트         //
export default function Authentication() {
  //          state: 화면 상태          //
  const [view, setView] = useState<"sign-in" | "sign-up">("sign-in");

  //          component: sign in card 컴포넌트          //
  const SignInCard = () => {
    //        render: sign in card 컴포넌트 렌더링          //
    return <div className="auth-card"></div>;
  };
  //          component: sign up card 컴포넌트          //
  const SignUpCard = () => {
    //        render: sign up card 컴포넌트 렌더링          //
    return <div className="auth-card"></div>;
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
