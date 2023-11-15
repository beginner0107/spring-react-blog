import React from "react";
import "./App.css";
import BoardItem from "components/BoardItem";
import { latestBoardListMock, top3BoardListMock } from "mocks";
import Top3Item from "components/Top3Item";

function App() {
  return (
    <>
      <div style={{ display: "flex", justifyContent: "center", gap: "24px" }}>
        {top3BoardListMock.map((top3ListItem) => (
          <Top3Item top3ListItem={top3ListItem} />
        ))}
      </div>
    </>
  );
}

export default App;
