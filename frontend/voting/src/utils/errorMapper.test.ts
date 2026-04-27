import { describe, it, expect } from "vitest";
import { getErrorMessage } from "./errorMapper";

describe("Utility: getErrorMessage", () => {
  it("deve retornar a mensagem correta para um código de erro conhecido (Happy Path)", () => {
    const result = getErrorMessage("DUPLICATE_VOTE");
    expect(result).toBe("Este CPF já registrou um voto nesta pauta.");

    expect(getErrorMessage("SESSION_ALREADY_EXISTS")).toBe(
      "Esta pauta já possui uma sessão de votação (aberta ou encerrada).",
    );
  });

  it("deve retornar a mensagem de erro desconhecido quando o código não existir no dicionário", () => {
    const result = getErrorMessage("BOTAO_QUEBRADO_ERROR");
    expect(result).toBe("Ocorreu um erro inesperado. Tente novamente mais tarde.");
  });

  it("deve retornar a mensagem de erro desconhecido quando nenhum código for fornecido (undefined)", () => {
    const result = getErrorMessage();
    expect(result).toBe("Ocorreu um erro inesperado. Tente novamente mais tarde.");
  });

  it("deve retornar a mensagem de erro desconhecido quando o código for uma string vazia (falsy)", () => {
    const result = getErrorMessage("");
    expect(result).toBe("Ocorreu um erro inesperado. Tente novamente mais tarde.");
  });
});
