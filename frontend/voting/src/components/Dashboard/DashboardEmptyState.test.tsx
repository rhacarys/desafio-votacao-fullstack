import { describe, it, expect } from "vitest";
import { screen } from "@testing-library/react";
import { renderWithProviders } from "../../tests/test-utils";
import { DashboardEmptyState } from "./DashboardEmptyState";

describe("Unit: DashboardEmptyState", () => {
  it("deve renderizar as mensagens de estado vazio corretamente", () => {
    renderWithProviders(<DashboardEmptyState />);

    expect(screen.getByText("Nenhuma pauta cadastrada ainda.")).toBeInTheDocument();
    expect(screen.getByText(/Clique em "Nova Pauta" para começar\./i)).toBeInTheDocument();
  });
});