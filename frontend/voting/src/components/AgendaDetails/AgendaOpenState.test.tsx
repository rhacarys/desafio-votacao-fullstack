import { describe, it, expect, vi, beforeEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { renderWithProviders } from "../../tests/test-utils";
import { AgendaOpenState } from "./AgendaOpenState";
import { votingService } from "../../services/votingService";

describe("Integration: AgendaOpenState", () => {
  const mockSessionId = 99;

  beforeEach(() => {
    vi.clearAllMocks();
    vi.spyOn(votingService, "registerVote").mockReset();
  });

  it("deve renderizar corretamente e desabilitar os botões se o CPF estiver vazio", () => {
    const closesAt = new Date(Date.now() + 60000).toISOString();
    renderWithProviders(<AgendaOpenState sessionId={mockSessionId} closesAt={closesAt} />);

    expect(screen.getByText("Registre seu Voto")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /SIM/i })).toBeDisabled();
    expect(screen.getByRole("button", { name: /NÃO/i })).toBeDisabled();
  });

  it("deve formatar e exibir o cronômetro corretamente", () => {
    const closesAt = new Date(Date.now() + 65000).toISOString();
    renderWithProviders(<AgendaOpenState sessionId={mockSessionId} closesAt={closesAt} />);

    expect(screen.getByText("A sessão será encerrada em:")).toBeInTheDocument();
    expect(screen.getByText(/01:0[4-5]/)).toBeInTheDocument();
  });

  it("deve permitir apenas números no input de CPF e habilitar botões com 11 dígitos", async () => {
    const user = userEvent.setup();
    const closesAt = new Date(Date.now() + 60000).toISOString();
    renderWithProviders(<AgendaOpenState sessionId={mockSessionId} closesAt={closesAt} />);

    const cpfInput = screen.getByRole("textbox", { name: /CPF/i });

    await user.type(cpfInput, "123abc456xy78901");

    expect(cpfInput).toHaveValue("12345678901");
    expect(screen.getByRole("button", { name: /SIM/i })).not.toBeDisabled();
    expect(screen.getByRole("button", { name: /NÃO/i })).not.toBeDisabled();
  });

  it("deve registrar o voto SIM com sucesso, exibir o feedback e limpar o CPF", async () => {
    const user = userEvent.setup();
    const closesAt = new Date(Date.now() + 60000).toISOString();
    vi.spyOn(votingService, "registerVote").mockResolvedValueOnce({});

    renderWithProviders(<AgendaOpenState sessionId={mockSessionId} closesAt={closesAt} />);

    const cpfInput = screen.getByRole("textbox", { name: /CPF/i });
    await user.type(cpfInput, "12345678901");

    const simButton = screen.getByRole("button", { name: /SIM/i });
    await user.click(simButton);

    await waitFor(() => {
      expect(votingService.registerVote).toHaveBeenCalledWith(mockSessionId, {
        associateCpf: "12345678901",
        choice: "YES",
      });
      expect(screen.getByText("Seu voto foi computado com sucesso!")).toBeInTheDocument();
      expect(cpfInput).toHaveValue("");
    });
  });

  it("deve registrar o voto NÃO e lidar com erros da API corretamente", async () => {
    const user = userEvent.setup();
    const closesAt = new Date(Date.now() + 60000).toISOString();

    const mockError = { uiMessage: "Este CPF já registrou um voto nesta pauta." };
    vi.spyOn(votingService, "registerVote").mockRejectedValueOnce(mockError);

    renderWithProviders(<AgendaOpenState sessionId={mockSessionId} closesAt={closesAt} />);

    const cpfInput = screen.getByRole("textbox", { name: /CPF/i });
    await user.type(cpfInput, "10987654321");

    const naoButton = screen.getByRole("button", { name: /NÃO/i });
    await user.click(naoButton);

    await waitFor(() => {
      expect(votingService.registerVote).toHaveBeenCalledWith(mockSessionId, {
        associateCpf: "10987654321",
        choice: "NO",
      });
      expect(screen.getByText("Este CPF já registrou um voto nesta pauta.")).toBeInTheDocument();
      expect(cpfInput).toHaveValue("10987654321");
    });
  });
});
