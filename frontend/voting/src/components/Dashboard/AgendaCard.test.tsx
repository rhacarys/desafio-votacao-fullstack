import { describe, it, expect, vi } from "vitest";
import { screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { renderWithProviders } from "../../tests/test-utils";
import { AgendaCard } from "./AgendaCard";

describe("Unit: AgendaCard", () => {
  const mockAgenda = {
    id: 42,
    title: "Pauta de Teste",
    description: "Descrição da pauta de teste estruturada.",
  };

  it("deve renderizar as informações da pauta corretamente", () => {
    renderWithProviders(<AgendaCard agenda={mockAgenda} onClick={vi.fn()} />);

    expect(screen.getByText("Pauta de Teste")).toBeInTheDocument();
    expect(screen.getByText("Descrição da pauta de teste estruturada.")).toBeInTheDocument();
    expect(screen.getByText("ID: #42")).toBeInTheDocument();
    expect(screen.getByText("Acessar Sessão")).toBeInTheDocument();
  });

  it("deve chamar a função onClick quando o card for clicado", async () => {
    const user = userEvent.setup();
    const mockOnClick = vi.fn();

    renderWithProviders(<AgendaCard agenda={mockAgenda} onClick={mockOnClick} />);

    const cardTitle = screen.getByText("Pauta de Teste");
    await user.click(cardTitle);

    expect(mockOnClick).toHaveBeenCalledTimes(1);
  });
});
