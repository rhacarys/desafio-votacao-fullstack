import { type ReactElement } from "react";
import { render, type RenderOptions } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { MemoryRouter } from "react-router-dom";
import { SnackbarProvider } from "../contexts/SnackbarContext";

const createTestQueryClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
      mutations: {
        retry: false,
      },
    },
  });

export function renderWithProviders(ui: ReactElement, options?: Omit<RenderOptions, "wrapper">) {
  const testQueryClient = createTestQueryClient();

  return render(ui, {
    wrapper: ({ children }) => (
      <QueryClientProvider client={testQueryClient}>
        <MemoryRouter>
          <SnackbarProvider>{children}</SnackbarProvider>
        </MemoryRouter>
      </QueryClientProvider>
    ),
    ...options,
  });
}

export * from "@testing-library/react";
export { userEvent } from "@testing-library/user-event";
