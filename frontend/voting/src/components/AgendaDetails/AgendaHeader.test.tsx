import { describe, it, expect } from "vitest";
import { screen } from "@testing-library/react";
import { renderWithProviders } from "../../tests/test-utils";
import { AgendaHeader } from "./AgendaHeader";

describe("Unit: AgendaHeader", () => {
  const defaultProps = {
    title: "Pauta de Orçamento 2027",
    description: "Aprovação do orçamento para o próximo ano fiscal.",
  };

  it("deve renderizar o título, descrição e status PENDING (Aguardando) corretamente", () => {
    renderWithProviders(<AgendaHeader {...defaultProps} status="PENDING" />);

    expect(screen.getByRole("heading", { name: "Pauta de Orçamento 2027" })).toBeInTheDocument();
    expect(screen.getByText("Aprovação do orçamento para o próximo ano fiscal.")).toBeInTheDocument();
    expect(screen.getByText("Aguardando")).toBeInTheDocument();
  });

  it("deve renderizar o status OPEN (Votação Aberta) corretamente", () => {
    renderWithProviders(<AgendaHeader {...defaultProps} status="OPEN" />);

    expect(screen.getByText("Votação Aberta")).toBeInTheDocument();
  });

  it("deve renderizar o status CLOSED (Encerrada) corretamente", () => {
    renderWithProviders(<AgendaHeader {...defaultProps} status="CLOSED" />);

    expect(screen.getByText("Encerrada")).toBeInTheDocument();
  });
});
