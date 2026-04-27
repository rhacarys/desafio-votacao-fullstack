import { Box, Stack, Typography, Paper } from "@mui/material";

interface AgendaHeaderProps {
  title: string;
  description: string;
  status: "PENDING" | "OPEN" | "CLOSED";
}

export function AgendaHeader({ title, description, status }: AgendaHeaderProps) {
  return (
    <Box sx={{ p: { xs: 4, md: 5 }, borderBottom: "1px solid", borderColor: "grey.100" }}>
      <Stack direction="row" sx={{ justifyContent: "space-between", alignItems: "flex-start", mb: 2 }}>
        <Typography variant="h4" sx={{ fontWeight: "bold", color: "text.primary" }}>
          {title}
        </Typography>
        <Paper
          elevation={0}
          sx={{
            px: 2,
            py: 0.5,
            bgcolor: status === "OPEN" ? "success.light" : status === "CLOSED" ? "grey.200" : "warning.light",
            color: status === "OPEN" ? "success.dark" : "text.secondary",
            fontWeight: "bold",
            borderRadius: 2,
          }}
        >
          {status === "PENDING" ? "Aguardando" : status === "OPEN" ? "Votação Aberta" : "Encerrada"}
        </Paper>
      </Stack>
      <Typography variant="subtitle1" sx={{ color: "text.secondary", lineHeight: 1.6 }}>
        {description}
      </Typography>
    </Box>
  );
}
