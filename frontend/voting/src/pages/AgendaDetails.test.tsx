import { describe, it, expect, vi, beforeEach } from "vitest";
import { screen } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { renderWithProviders } from "../tests/test-utils";
import { AgendaDetails } from "./AgendaDetails";
import { votingService } from "../services/votingService";

const mockNavigate = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => ({ id: "1" }),
  };
});

describe("Integration: AgendaDetails", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.spyOn(votingService, "getAgendaDetails").mockReset();
    vi.spyOn(votingService, "openSession").mockReset();
    vi.spyOn(votingService, "registerVote").mockReset();
  });

  it("deve exibir o loading inicialmente", () => {
    vi.spyOn(votingService, "getAgendaDetails").mockReturnValue(new Promise(() => {}));

    renderWithProviders(<AgendaDetails />);

    expect(screen.getByRole("progressbar")).toBeInTheDocument();
  });

  it("deve exibir mensagem de erro se a API falhar", async () => {
    vi.spyOn(votingService, "getAgendaDetails").mockRejectedValueOnce(new Error("Network Error"));

    renderWithProviders(<AgendaDetails />);

    expect(await screen.findByText(/Erro ao carregar os detalhes da pauta/i)).toBeInTheDocument();
  });

  it("deve navegar de volta para o dashboard ao clicar no botão de voltar", async () => {
    const user = userEvent.setup();
    vi.spyOn(votingService, "getAgendaDetails").mockResolvedValueOnce({
      id: 1,
      title: "Pauta 1",
      description: "Desc",
      status: "PENDING",
    });

    renderWithProviders(<AgendaDetails />);

    const backButton = await screen.findByRole("button", { name: /Voltar para o Dashboard/i });
    await user.click(backButton);

    expect(mockNavigate).toHaveBeenCalledWith("/");
  });

  it("deve renderizar o estado PENDING corretamente", async () => {
    vi.spyOn(votingService, "getAgendaDetails").mockResolvedValueOnce({
      id: 1,
      title: "Pauta Pendente",
      description: "Descrição da pauta pendente",
      status: "PENDING",
    });

    renderWithProviders(<AgendaDetails />);

    expect(await screen.findByText("Pauta Pendente")).toBeInTheDocument();
    expect(screen.getByText("Aguardando")).toBeInTheDocument();
    expect(screen.getByText("Iniciar Votação")).toBeInTheDocument();
  });

  it("deve renderizar o estado OPEN corretamente", async () => {
    vi.spyOn(votingService, "getAgendaDetails").mockResolvedValueOnce({
      id: 1,
      title: "Pauta Aberta",
      description: "Descrição da pauta aberta",
      status: "OPEN",
      sessionId: 100,
      closesAt: new Date(Date.now() + 60000).toISOString(),
    });

    renderWithProviders(<AgendaDetails />);

    expect(await screen.findByText("Pauta Aberta")).toBeInTheDocument();
    expect(screen.getByText("Votação Aberta")).toBeInTheDocument();
    expect(screen.getByText("Registre seu Voto")).toBeInTheDocument();
  });

  it("deve renderizar o estado CLOSED corretamente", async () => {
    vi.spyOn(votingService, "getAgendaDetails").mockResolvedValueOnce({
      id: 1,
      title: "Pauta Encerrada",
      description: "Descrição da pauta encerrada",
      status: "CLOSED",
      yesVotes: 10,
      noVotes: 5,
      totalVotes: 15,
    });

    renderWithProviders(<AgendaDetails />);

    expect(await screen.findByText("Pauta Encerrada")).toBeInTheDocument();
    expect(screen.getByText("Encerrada")).toBeInTheDocument();
    expect(screen.getByText("Resultados Finais")).toBeInTheDocument();
    expect(screen.getByText("Total de votos: 15")).toBeInTheDocument();
  });
});
