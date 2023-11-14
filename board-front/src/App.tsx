import React from "react";
import "./App.css";
import BoardItem from "components/BoardItem";
import { latestBoardListMock } from "mocks";

function App() {
  return (
    <>
      {latestBoardListMock.map((boardListItem) => (
        <BoardItem boardListItem={boardListItem} />
      ))}
    </>
  );
}

export default App;
