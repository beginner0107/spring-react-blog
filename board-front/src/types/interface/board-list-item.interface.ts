export interface BoardListItem {
  boardNumber: number;
  title: string;
  content: string;
  boardTitleImage: string | null;
  favoriteCount: number;
  commentCount: number;
  viewCount: number;
  writeDateTime: string;
  writeNickname: string;
  writerProfileImage: string | null;
}
