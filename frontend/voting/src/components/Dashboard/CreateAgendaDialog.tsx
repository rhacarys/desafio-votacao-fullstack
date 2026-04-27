import { useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Stack,
  CircularProgress,
} from "@mui/material";
import { useCreateAgenda } from "../../hooks/useVoting";

interface CreateAgendaDialogProps {
  open: boolean;
  onClose: () => void;
}

export function CreateAgendaDialog({ open, onClose }: CreateAgendaDialogProps) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  const { mutate, isPending } = useCreateAgenda();

  const handleClose = () => {
    if (!isPending) {
      setTitle("");
      setDescription("");
      onClose();
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutate(
      { title, description },
      {
        onSuccess: () => {
          handleClose();
        },
      },
    );
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <form onSubmit={handleSubmit}>
        <DialogTitle sx={{ fontWeight: "bold" }}>Criar Nova Pauta</DialogTitle>
        <DialogContent dividers>
          <Stack spacing={3} sx={{ mt: 1 }}>
            <TextField
              label="Título"
              required
              fullWidth
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              disabled={isPending}
              autoFocus
            />
            <TextField
              label="Descrição"
              required
              fullWidth
              multiline
              rows={4}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              disabled={isPending}
            />
          </Stack>
        </DialogContent>
        <DialogActions sx={{ px: 3, py: 2 }}>
          <Button onClick={handleClose} disabled={isPending} color="inherit">
            Cancelar
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={isPending || !title.trim() || !description.trim()}
            startIcon={isPending ? <CircularProgress size={20} color="inherit" /> : null}
          >
            {isPending ? "Criando..." : "Criar Pauta"}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}
