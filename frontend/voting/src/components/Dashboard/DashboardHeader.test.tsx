import { describe, it, expect, vi } from "vitest";
import { screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { renderWithProviders } from "../../tests/test-utils";
import { DashboardHeader } from "./DashboardHeader";

describe("Unit: DashboardHeader", () => {
  it("deve renderizar o título e o botão corretamente", () => {
    renderWithProviders(<DashboardHeader onOpenCreateDialog={vi.fn()} />);

    expect(screen.getByRole("heading", { name: /Pautas de Votação/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /Nova Pauta/i })).toBeInTheDocument();
  });

  it("deve chamar a função onOpenCreateDialog ao clicar no botão", async () => {
    const user = userEvent.setup();
    const mockOnOpen = vi.fn();

    renderWithProviders(<DashboardHeader onOpenCreateDialog={mockOnOpen} />);

    const button = screen.getByRole("button", { name: /Nova Pauta/i });
    await user.click(button);

    expect(mockOnOpen).toHaveBeenCalledTimes(1);
  });
});