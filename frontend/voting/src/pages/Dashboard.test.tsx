import { describe, it, expect, vi, beforeEach } from "vitest";
import { screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { renderWithProviders } from "../tests/test-utils";
import { Dashboard } from "./Dashboard";
import { votingService } from "../services/votingService";

vi.mock("../services/votingService", () => ({
  votingService: {
    getAgendas: vi.fn(),
    createAgenda: vi.fn(),
  },
}));

const mockNavigate = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe("Integration: Dashboard", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("deve exibir o loading inicialmente", () => {
    vi.mocked(votingService.getAgendas).mockReturnValue(new Promise(() => {}));

    renderWithProviders(<Dashboard />);

    expect(screen.getByRole("progressbar")).toBeInTheDocument();
  });

  it("deve exibir mensagem de erro se a API falhar", async () => {
    vi.mocked(votingService.getAgendas).mockRejectedValueOnce(new Error("Network Error"));

    renderWithProviders(<Dashboard />);

    expect(await screen.findByText(/Não foi possível conectar ao servidor/i)).toBeInTheDocument();
  });

  it("deve exibir o empty state quando não houver pautas", async () => {
    vi.mocked(votingService.getAgendas).mockResolvedValueOnce([]);

    renderWithProviders(<Dashboard />);

    expect(await screen.findByText(/Nenhuma pauta cadastrada ainda/i)).toBeInTheDocument();
  });

  it("deve renderizar a lista de pautas corretamente", async () => {
    const mockAgendas = [
      { id: 1, title: "Pauta de Teste 1", description: "Descrição 1" },
      { id: 2, title: "Pauta de Teste 2", description: "Descrição 2" },
    ];
    vi.mocked(votingService.getAgendas).mockResolvedValueOnce(mockAgendas);

    renderWithProviders(<Dashboard />);

    expect(await screen.findByText("Pauta de Teste 1")).toBeInTheDocument();
    expect(screen.getByText("Pauta de Teste 2")).toBeInTheDocument();
  });

  it("deve navegar para os detalhes da pauta ao clicar no card", async () => {
    const user = userEvent.setup();
    vi.mocked(votingService.getAgendas).mockResolvedValueOnce([
      { id: 10, title: "Pauta Navegação", description: "Desc" },
    ]);

    renderWithProviders(<Dashboard />);

    const card = await screen.findByText("Pauta Navegação");
    await user.click(card);

    expect(mockNavigate).toHaveBeenCalledWith("/agenda/10");
  });

  it("deve abrir o modal de criação ao clicar em Nova Pauta", async () => {
    const user = userEvent.setup();
    vi.mocked(votingService.getAgendas).mockResolvedValueOnce([]);

    renderWithProviders(<Dashboard />);

    await screen.findByText(/Nenhuma pauta cadastrada ainda/i);

    const btnNovaPauta = screen.getByRole("button", { name: /Nova Pauta/i });
    await user.click(btnNovaPauta);

    expect(screen.getByRole("heading", { name: /Criar Nova Pauta/i })).toBeInTheDocument();
  });
});
