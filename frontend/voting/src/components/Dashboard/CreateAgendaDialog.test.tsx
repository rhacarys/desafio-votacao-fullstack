import { describe, it, expect, vi, beforeEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { renderWithProviders } from "../../tests/test-utils";
import { CreateAgendaDialog } from "./CreateAgendaDialog";
import { votingService } from "../../services/votingService";

vi.mock("../../services/votingService", () => ({
  votingService: {
    createAgenda: vi.fn(),
  },
}));

describe("Integration: CreateAgendaDialog", () => {
  const mockOnClose = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("deve renderizar o modal e manter o botão desabilitado se os campos estiverem vazios", () => {
    renderWithProviders(<CreateAgendaDialog open={true} onClose={mockOnClose} />);

    expect(screen.getByRole("heading", { name: /criar nova pauta/i })).toBeInTheDocument();
    expect(screen.getByRole("textbox", { name: /título/i })).toBeInTheDocument();
    expect(screen.getByRole("textbox", { name: /descrição/i })).toBeInTheDocument();

    const submitButton = screen.getByRole("button", { name: /criar pauta/i });
    expect(submitButton).toBeDisabled();
  });

  it("deve chamar a API, exibir feedback de sucesso e fechar o modal (Happy Path)", async () => {
    const user = userEvent.setup();
    vi.mocked(votingService.createAgenda).mockResolvedValueOnce({ id: 1, title: "Teste", description: "Descrição de teste" });

    renderWithProviders(<CreateAgendaDialog open={true} onClose={mockOnClose} />);

    await user.type(screen.getByRole("textbox", { name: /título/i }), "Pauta de Teste");
    await user.type(screen.getByRole("textbox", { name: /descrição/i }), "Descrição da pauta");

    const submitButton = screen.getByRole("button", { name: /criar pauta/i });
    expect(submitButton).not.toBeDisabled();

    await user.click(submitButton);
    await waitFor(() => {
      expect(votingService.createAgenda).toHaveBeenCalledWith("Pauta de Teste", "Descrição da pauta");
      expect(mockOnClose).toHaveBeenCalledTimes(1);
      expect(screen.getByText("Pauta criada com sucesso!")).toBeInTheDocument();
    });
  });

  it("deve exibir feedback de erro e não fechar o modal em caso de falha da API", async () => {
    const user = userEvent.setup();
    vi.mocked(votingService.createAgenda).mockRejectedValueOnce(new Error("API Error"));

    renderWithProviders(<CreateAgendaDialog open={true} onClose={mockOnClose} />);

    await user.type(screen.getByRole("textbox", { name: /título/i }), "Pauta Falha");
    await user.type(screen.getByRole("textbox", { name: /descrição/i }), "Vai dar erro");
    await user.click(screen.getByRole("button", { name: /criar pauta/i }));

    await waitFor(() => {
      expect(screen.getByText("Erro ao criar pauta.")).toBeInTheDocument();
      expect(mockOnClose).not.toHaveBeenCalled();
    });
  });
});
