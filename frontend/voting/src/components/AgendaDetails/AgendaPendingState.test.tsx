import { describe, it, expect, vi, beforeEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { renderWithProviders } from "../../tests/test-utils";
import { AgendaPendingState } from "./AgendaPendingState";
import { votingService } from "../../services/votingService";

describe("Integration: AgendaPendingState", () => {
  const mockAgendaId = 42;

  beforeEach(() => {
    vi.clearAllMocks();
    vi.spyOn(votingService, "openSession").mockReset();
  });

  it("deve renderizar o componente corretamente com valor padrão de 1 minuto", () => {
    renderWithProviders(<AgendaPendingState agendaId={mockAgendaId} />);

    expect(screen.getByText("Iniciar Votação")).toBeInTheDocument();

    const input = screen.getByRole("spinbutton", { name: /Duração em Minutos/i });
    expect(input).toHaveValue(1);

    expect(screen.getByRole("button", { name: /Abrir Sessão/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /Abrir Sessão/i })).not.toBeDisabled();
  });

  it("deve iniciar a sessão com a duração alterada e exibir feedback de sucesso", async () => {
    const user = userEvent.setup();
    vi.spyOn(votingService, "openSession").mockResolvedValueOnce({
      sessionId: 0,
      agendaId: 0,
      opensAt: "",
      closesAt: "",
    });

    renderWithProviders(<AgendaPendingState agendaId={mockAgendaId} />);

    const input = screen.getByRole("spinbutton", { name: /Duração em Minutos/i });
    await user.clear(input);
    await user.type(input, "15");

    const submitButton = screen.getByRole("button", { name: /Abrir Sessão/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(votingService.openSession).toHaveBeenCalledWith(mockAgendaId, 15);
      expect(screen.getByText("Sessão de votação iniciada!")).toBeInTheDocument();
    });
  });

  it("deve usar 1 minuto como fallback caso o input fique vazio ou inválido", async () => {
    const user = userEvent.setup();
    vi.spyOn(votingService, "openSession").mockResolvedValueOnce({
      sessionId: 0,
      agendaId: 0,
      opensAt: "",
      closesAt: "",
    });

    renderWithProviders(<AgendaPendingState agendaId={mockAgendaId} />);

    const input = screen.getByRole("spinbutton", { name: /Duração em Minutos/i });
    await user.clear(input);

    const submitButton = screen.getByRole("button", { name: /Abrir Sessão/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(votingService.openSession).toHaveBeenCalledWith(mockAgendaId, 1);
    });
  });

  it("deve exibir snackbar de erro quando a chamada da API falhar", async () => {
    const user = userEvent.setup();
    const mockError = { response: { data: { message: "Erro simulado do servidor" } } };
    vi.spyOn(votingService, "openSession").mockRejectedValueOnce(mockError);

    renderWithProviders(<AgendaPendingState agendaId={mockAgendaId} />);

    const submitButton = screen.getByRole("button", { name: /Abrir Sessão/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(votingService.openSession).toHaveBeenCalledWith(mockAgendaId, 1);
      expect(screen.getByText("Erro simulado do servidor")).toBeInTheDocument();
    });
  });

  it("deve usar mensagem de erro padrão caso o erro não tenha mensagem específica", async () => {
    const user = userEvent.setup();
    vi.spyOn(votingService, "openSession").mockRejectedValueOnce(new Error("Network Error"));

    renderWithProviders(<AgendaPendingState agendaId={mockAgendaId} />);

    const submitButton = screen.getByRole("button", { name: /Abrir Sessão/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("Erro ao iniciar a sessão. Tente novamente.")).toBeInTheDocument();
    });
  });
});
