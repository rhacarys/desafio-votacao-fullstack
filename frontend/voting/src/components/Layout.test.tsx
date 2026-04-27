import { describe, it, expect } from "vitest";
import { screen } from "@testing-library/react";
import { Routes, Route } from "react-router-dom";
import { renderWithProviders } from "../tests/test-utils";
import { Layout } from "./Layout";

describe("Unit/Integration: Layout", () => {
  it("deve renderizar o cabeçalho (AppBar) com o título corretamente", () => {
    renderWithProviders(<Layout />);

    expect(screen.getByRole("heading", { name: /Desafio de Votação/i })).toBeInTheDocument();
    expect(screen.getByTestId("HowToVoteIcon")).toBeInTheDocument();
  });

  it("deve renderizar o conteúdo das rotas filhas através do Outlet", () => {
    renderWithProviders(
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<div data-testid="child-content">Conteúdo da Rota Filha</div>} />
        </Route>
      </Routes>,
    );

    expect(screen.getByTestId("child-content")).toBeInTheDocument();
    expect(screen.getByText("Conteúdo da Rota Filha")).toBeInTheDocument();

    // O cabeçalho deve continuar visível junto com o conteúdo filho
    expect(screen.getByRole("heading", { name: /Desafio de Votação/i })).toBeInTheDocument();
  });
});
