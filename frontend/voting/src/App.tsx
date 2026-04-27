import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Layout } from "./components/Layout";
import { Dashboard } from "./pages/Dashboard";
import { AgendaDetails } from "./pages/AgendaDetails";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="agenda/:id" element={<AgendaDetails />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
