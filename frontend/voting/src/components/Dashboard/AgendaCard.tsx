import { Card, CardContent, CardActions, Grid, Typography, Box, Stack } from "@mui/material";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";

interface AgendaCardProps {
  agenda: {
    id: number;
    title: string;
    description: string;
  };
  onClick: () => void;
}

export function AgendaCard({ agenda, onClick }: AgendaCardProps) {
  return (
    <Grid size={{ xs: 12, sm: 6, md: 4 }}>
      <Card
        elevation={0}
        onClick={onClick}
        sx={{
          height: "100%",
          display: "flex",
          flexDirection: "column",
          borderRadius: 3,
          border: "1px solid",
          borderColor: "divider",
          cursor: "pointer",
          transition: "all 0.2s ease-in-out",
          "&:hover": {
            borderColor: "primary.main",
            boxShadow: 2,
          },
        }}
      >
        <CardContent sx={{ flexGrow: 1, p: 3, pb: 0 }}>
          <Typography variant="h6" sx={{ fontWeight: "bold" }} gutterBottom>
            {agenda.title}
          </Typography>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              display: "-webkit-box",
              WebkitLineClamp: 3,
              WebkitBoxOrient: "vertical",
              overflow: "hidden",
              lineHeight: 1.6,
            }}
          >
            {agenda.description}
          </Typography>
        </CardContent>

        <CardActions
          sx={{
            justifyContent: "space-between",
            px: 3,
            py: 2,
            mt: 2,
            borderTop: "1px solid",
            borderColor: "grey.50",
          }}
        >
          <Box
            sx={{
              bgcolor: "grey.100",
              color: "text.secondary",
              px: 1,
              py: 0.5,
              borderRadius: 1,
              fontSize: "0.75rem",
              fontWeight: "bold",
            }}
          >
            ID: #{agenda.id}
          </Box>
          <Stack direction="row" spacing={0.5} sx={{ alignItems: "center", color: "primary.main" }}>
            <Typography variant="body2" sx={{ fontWeight: "medium" }}>
              Acessar Sessão
            </Typography>
            <ArrowForwardIcon sx={{ fontSize: 16 }} />
          </Stack>
        </CardActions>
      </Card>
    </Grid>
  );
}
