import { createElement } from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { renderHook, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useAgendas, useAgendaDetails, useCreateAgenda, useVote, useOpenSession } from "./useVoting";
import { votingService } from "../services/votingService";

vi.mock("../services/votingService", () => ({
  votingService: {
    getAgendas: vi.fn(),
    getAgendaDetails: vi.fn(),
    createAgenda: vi.fn(),
    registerVote: vi.fn(),
    openSession: vi.fn(),
  },
}));

const createTestQueryClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

const wrapper = ({ children }: { children: React.ReactNode }) => {
  const testQueryClient = createTestQueryClient();
  return createElement(QueryClientProvider, { client: testQueryClient }, children);
};

describe("Hooks: useVoting", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("useAgendas", () => {
    it("deve buscar a lista de pautas corretamente", async () => {
      const mockAgendas = [{ id: 1, title: "Pauta 1", description: "Descrição da Pauta 1" }];
      vi.spyOn(votingService, "getAgendas").mockResolvedValueOnce(mockAgendas);

      const { result } = renderHook(() => useAgendas(), { wrapper });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(votingService.getAgendas).toHaveBeenCalledTimes(1);
      expect(result.current.data).toEqual(mockAgendas);
    });
  });

  describe("useAgendaDetails", () => {
    it("deve buscar os detalhes de uma pauta específica", async () => {
      const mockAgendaDetails = {
        id: 10,
        title: "Pauta 10",
        description: "Descrição da Pauta 10",
        status: "CLOSED" as const,
      };
      vi.spyOn(votingService, "getAgendaDetails").mockResolvedValueOnce(mockAgendaDetails);

      const { result } = renderHook(() => useAgendaDetails(10), { wrapper });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(votingService.getAgendaDetails).toHaveBeenCalledWith(10);
      expect(result.current.data).toEqual(mockAgendaDetails);
    });
  });

  describe("useCreateAgenda", () => {
    it("deve criar uma pauta e invalidar o cache da lista de pautas", async () => {
      vi.spyOn(votingService, "createAgenda").mockResolvedValueOnce({
        id: 0,
        title: "",
        description: "",
      });
      const invalidateSpy = vi.spyOn(QueryClient.prototype, "invalidateQueries");

      const { result } = renderHook(() => useCreateAgenda(), { wrapper });

      result.current.mutate({ title: "Nova Pauta", description: "Desc" });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(votingService.createAgenda).toHaveBeenCalledWith("Nova Pauta", "Desc");
      expect(invalidateSpy).toHaveBeenCalledWith({ queryKey: ["agendas"] });
    });
  });

  describe("useVote", () => {
    it("deve registrar o voto e invalidar o cache da lista de pautas", async () => {
      vi.spyOn(votingService, "registerVote").mockResolvedValueOnce({});
      const invalidateSpy = vi.spyOn(QueryClient.prototype, "invalidateQueries");

      const { result } = renderHook(() => useVote(), { wrapper });

      const mockVote = { associateCpf: "12345678901", choice: "YES" as const };
      result.current.mutate({ sessionId: 99, vote: mockVote });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(votingService.registerVote).toHaveBeenCalledWith(99, mockVote);
      expect(invalidateSpy).toHaveBeenCalledWith({ queryKey: ["agendas"] });
    });
  });

  describe("useOpenSession", () => {
    it("deve abrir uma sessão e invalidar o cache da lista e dos detalhes da pauta", async () => {
      vi.spyOn(votingService, "openSession").mockResolvedValueOnce({
        sessionId: 0,
        agendaId: 0,
        opensAt: "",
        closesAt: "",
      });
      const invalidateSpy = vi.spyOn(QueryClient.prototype, "invalidateQueries");

      const { result } = renderHook(() => useOpenSession(), { wrapper });

      result.current.mutate({ agendaId: 42, durationInMinutes: 5 });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(votingService.openSession).toHaveBeenCalledWith(42, 5);
      expect(invalidateSpy).toHaveBeenCalledWith({ queryKey: ["agendas"] });
      expect(invalidateSpy).toHaveBeenCalledWith({ queryKey: ["agenda", 42] });
    });
  });
});
