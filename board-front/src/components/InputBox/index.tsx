import React, { forwardRef } from "react";
import "./style.css";

interface Props {}

const InputBox = forwardRef<HTMLInputElement, Props>((props, ref) => {
  return (
    <div className="inputbox">
      <div className="inputbox-label">{"비밀번호*"}</div>
      <div className="inputbox-container">
        <input className="input" ref={ref} />
        <div className="icon-button">
          <div className="icon eye-light-off-icon"></div>
        </div>
      </div>
      <div className="inputbox-message">
        {"비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다."}
      </div>
    </div>
  );
});

export default InputBox;
