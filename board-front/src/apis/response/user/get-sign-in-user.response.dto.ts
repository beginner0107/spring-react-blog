import ResponseDto from "../response.dto";

export default interface GetSignInUserResponseDto extends ResponseDto {
  email: string;
  nickname: string;
  profileImage: string;
}
