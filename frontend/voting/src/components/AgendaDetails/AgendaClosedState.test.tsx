import { describe, it, expect } from "vitest";
import { screen } from "@testing-library/react";
import { renderWithProviders } from "../../tests/test-utils";
import { AgendaClosedState } from "./AgendaClosedState";

describe("Unit: AgendaClosedState", () => {
  it("deve renderizar os resultados e calcular as porcentagens corretamente", () => {
    renderWithProviders(<AgendaClosedState yesVotes={15} noVotes={5} totalVotes={20} />);

    expect(screen.getByText("Resultados Finais")).toBeInTheDocument();
    expect(screen.getByText("SIM (15)")).toBeInTheDocument();
    expect(screen.getByText("75%")).toBeInTheDocument();
    expect(screen.getByText("NÃO (5)")).toBeInTheDocument();
    expect(screen.getByText("25%")).toBeInTheDocument();
    expect(screen.getByText("Total de votos: 20")).toBeInTheDocument();
  });

  it("deve lidar corretamente com 0 votos totais (evitar NaN/Infinity)", () => {
    renderWithProviders(<AgendaClosedState yesVotes={0} noVotes={0} totalVotes={0} />);

    expect(screen.getByText("SIM (0)")).toBeInTheDocument();
    expect(screen.getByText("NÃO (0)")).toBeInTheDocument();
    expect(screen.getAllByText("0%")).toHaveLength(2);
    expect(screen.getByText("Total de votos: 0")).toBeInTheDocument();
  });
});
