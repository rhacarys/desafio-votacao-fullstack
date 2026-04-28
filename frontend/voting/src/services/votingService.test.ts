import { describe, it, expect, vi, beforeEach } from "vitest";
import { votingService } from "./votingService";
import { api } from "./api";

vi.mock("./api", () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe("Service: votingService", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("deve buscar a lista de pautas", async () => {
    const mockResponse = [{ id: 1, title: "Pauta 1" }];
    vi.mocked(api.get).mockResolvedValueOnce({ data: mockResponse });

    const result = await votingService.getAgendas();

    expect(api.get).toHaveBeenCalledWith("/agendas");
    expect(result).toEqual(mockResponse);
  });

  it("deve buscar os detalhes de uma pauta específica", async () => {
    const mockResponse = { id: 5, title: "Pauta 5", status: "PENDING" };
    vi.mocked(api.get).mockResolvedValueOnce({ data: mockResponse });

    const result = await votingService.getAgendaDetails(5);

    expect(api.get).toHaveBeenCalledWith("/agendas/5");
    expect(result).toEqual(mockResponse);
  });

  it("deve criar uma nova pauta", async () => {
    const mockResponse = { id: 10, title: "Título Teste", description: "Descrição Teste" };
    vi.mocked(api.post).mockResolvedValueOnce({ data: mockResponse });

    const result = await votingService.createAgenda("Título Teste", "Descrição Teste");

    expect(api.post).toHaveBeenCalledWith("/agendas", {
      title: "Título Teste",
      description: "Descrição Teste",
    });
    expect(result).toEqual(mockResponse);
  });

  it("deve abrir uma sessão de votação com a duração informada", async () => {
    const mockResponse = { id: 100, agendaId: 2, closesAt: "2026-04-27T20:00:00Z" };
    vi.mocked(api.post).mockResolvedValueOnce({ data: mockResponse });

    const result = await votingService.openSession(2, 15);

    expect(api.post).toHaveBeenCalledWith("/sessions/open", {
      agendaId: 2,
      durationInMinutes: 15,
    });
    expect(result).toEqual(mockResponse);
  });

  it("deve abrir uma sessão de votação com a duração padrão (1 minuto) se omitida", async () => {
    const mockResponse = { id: 101, agendaId: 3 };
    vi.mocked(api.post).mockResolvedValueOnce({ data: mockResponse });

    const result = await votingService.openSession(3);

    expect(api.post).toHaveBeenCalledWith("/sessions/open", {
      agendaId: 3,
      durationInMinutes: 1,
    });
    expect(result).toEqual(mockResponse);
  });

  it("deve registrar um voto corretamente", async () => {
    const mockResponse = { success: true };
    vi.mocked(api.post).mockResolvedValueOnce({ data: mockResponse });

    const mockVote = { associateCpf: "12345678901", choice: "YES" as const };
    const result = await votingService.registerVote(50, mockVote);

    expect(api.post).toHaveBeenCalledWith("/sessions/50/votes", mockVote);
    expect(result).toEqual(mockResponse);
  });
});
