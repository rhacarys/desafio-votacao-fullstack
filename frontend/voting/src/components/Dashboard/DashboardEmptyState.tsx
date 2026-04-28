import { Paper, Typography } from "@mui/material";
import ArticleOutlinedIcon from "@mui/icons-material/ArticleOutlined";

export function DashboardEmptyState() {
  return (
    <Paper
      elevation={0}
      sx={{
        textAlign: "center",
        py: 8,
        borderRadius: 3,
        border: "1px solid",
        borderColor: "divider",
      }}
    >
      <ArticleOutlinedIcon sx={{ fontSize: 48, color: "text.disabled", mb: 2 }} />
      <Typography variant="subtitle1" sx={{ color: "text.secondary", fontWeight: "medium" }}>
        Nenhuma pauta cadastrada ainda.
      </Typography>
      <Typography variant="body2" sx={{ color: "text.disabled", mt: 0.5 }}>
        Clique em "Nova Pauta" para começar.
      </Typography>
    </Paper>
  );
}
