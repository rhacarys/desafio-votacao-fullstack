import { describe, it, expect } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import { userEvent } from "@testing-library/user-event";
import { SnackbarProvider, useGlobalSnackbar } from "./SnackbarContext";

const TestComponent = () => {
  const { showSnackbar } = useGlobalSnackbar();
  return (
    <div>
      <button onClick={() => showSnackbar("Mensagem Sucesso")}>Disparar Sucesso</button>
      <button onClick={() => showSnackbar("Mensagem Erro", "error")}>Disparar Erro</button>
    </div>
  );
};

describe("Context: SnackbarContext", () => {
  it("não deve exibir o snackbar inicialmente", () => {
    render(
      <SnackbarProvider>
        <TestComponent />
      </SnackbarProvider>,
    );

    expect(screen.queryByRole("alert")).not.toBeInTheDocument();
  });

  it("deve exibir a mensagem de sucesso corretamente ao ser chamado", async () => {
    const user = userEvent.setup();
    render(
      <SnackbarProvider>
        <TestComponent />
      </SnackbarProvider>,
    );

    const button = screen.getByRole("button", { name: "Disparar Sucesso" });
    await user.click(button);

    const alert = screen.getByRole("alert");
    expect(alert).toBeInTheDocument();
    expect(alert).toHaveTextContent("Mensagem Sucesso");
  });

  it("deve exibir a mensagem de erro com a severidade correta", async () => {
    const user = userEvent.setup();
    render(
      <SnackbarProvider>
        <TestComponent />
      </SnackbarProvider>,
    );

    const button = screen.getByRole("button", { name: "Disparar Erro" });
    await user.click(button);

    const alert = screen.getByRole("alert");
    expect(alert).toBeInTheDocument();
    expect(alert).toHaveTextContent("Mensagem Erro");
  });

  it("deve fechar o snackbar ao clicar no ícone de fechar", async () => {
    const user = userEvent.setup();
    render(
      <SnackbarProvider>
        <TestComponent />
      </SnackbarProvider>,
    );

    await user.click(screen.getByRole("button", { name: "Disparar Sucesso" }));
    expect(screen.getByRole("alert")).toBeInTheDocument();

    const closeButton = screen.getByRole("button", { name: /close/i });
    await user.click(closeButton);

    await waitFor(() => {
      expect(screen.queryByRole("alert")).not.toBeInTheDocument();
    });
  });
});
