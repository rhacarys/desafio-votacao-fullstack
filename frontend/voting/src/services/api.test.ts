import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { api } from "./api";

describe("Interceptor: api", () => {
  const originalAdapter = api.defaults.adapter;

  beforeEach(() => {
    vi.spyOn(console, "error").mockImplementation(() => {});
  });

  afterEach(() => {
    api.defaults.adapter = originalAdapter;
    vi.restoreAllMocks();
  });

  it("deve retornar a resposta original em caso de sucesso", async () => {
    api.defaults.adapter = async (config) => {
      return {
        data: { success: true },
        status: 200,
        statusText: "OK",
        headers: {},
        config: config as any,
        request: {},
      };
    };

    const response = await api.get("/test");
    expect(response.data.success).toBe(true);
  });

  it("deve definir a uiMessage como NETWORK_ERROR quando não houver resposta do servidor", async () => {
    api.defaults.adapter = async () => {
      const error = new Error("Network Error");
      throw error;
    };

    await expect(api.get("/test")).rejects.toMatchObject({
      uiMessage: "Erro de conexão. Verifique sua internet.",
    });
  });

  it("deve definir a uiMessage usando o code retornado pelo backend", async () => {
    api.defaults.adapter = async (config) => {
      const error: any = new Error("Request failed");
      error.response = {
        data: { code: "SESSION_CLOSED" },
        status: 422,
        statusText: "Unprocessable Entity",
        headers: {},
        config,
      };
      throw error;
    };

    await expect(api.get("/test")).rejects.toMatchObject({
      uiMessage: "A sessão de votação já foi encerrada.",
    });
  });

  it("deve definir a uiMessage como AGENDA_NOT_FOUND para erros 404 sem code", async () => {
    api.defaults.adapter = async (config) => {
      const error: any = new Error("Not Found");
      error.response = {
        data: {},
        status: 404,
        statusText: "Not Found",
        headers: {},
        config,
      };
      throw error;
    };

    await expect(api.get("/test")).rejects.toMatchObject({
      uiMessage: "A pauta solicitada não foi encontrada.",
    });
  });

  it("deve definir a uiMessage como UNKNOWN_ERROR para outros erros sem mapeamento", async () => {
    api.defaults.adapter = async (config) => {
      const error: any = new Error("Internal Server Error");
      error.response = {
        data: {},
        status: 500,
        statusText: "Internal Server Error",
        headers: {},
        config,
      };
      throw error;
    };

    await expect(api.get("/test")).rejects.toMatchObject({
      uiMessage: "Ocorreu um erro inesperado. Tente novamente mais tarde.",
    });
  });
});
