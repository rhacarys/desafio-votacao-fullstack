import { Stack, Typography, Button } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";

interface DashboardHeaderProps {
  onOpenCreateDialog: () => void;
}

export function DashboardHeader({ onOpenCreateDialog }: DashboardHeaderProps) {
  return (
    <Stack direction="row" sx={{ alignItems: "center", justifyContent: "space-between", mb: 4 }}>
      <Typography variant="h5" sx={{ fontWeight: "bold", color: "text.primary" }}>
        Pautas de Votação
      </Typography>
      <Button
        variant="contained"
        startIcon={<AddIcon />}
        onClick={onOpenCreateDialog}
        sx={{ borderRadius: 2, textTransform: "none", fontWeight: "bold" }}
      >
        Nova Pauta
      </Button>
    </Stack>
  );
}
