/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [],
  theme: {
    extend: {
      colors: {
        basilisk: {
          // Paleta principal Basilisk
          900: "#0D3B3B", // Teal Profundo
          700: "#1E5C5C", // Teal Médio
          500: "#3A8585", // Teal Claro
          400: "#5BA3A0", // Teal Highlight
          metal: "#8B8B78", // Circuito Metálico
          ice: "#EAF2F2", // Gelo Teal
          cream: "#F5F0E6", // Creme Quente
        },
      },
      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"],
        mono: ["JetBrains Mono", "Fira Code", "monospace"],
      },
      fontSize: {
        "display-lg": ["36px", { lineHeight: "1.2", fontWeight: "600" }],
        "display-md": ["28px", { lineHeight: "1.3", fontWeight: "600" }],
        "display-sm": ["24px", { lineHeight: "1.4", fontWeight: "600" }],
        "body-lg": ["16px", { lineHeight: "1.6" }],
        "body-md": ["14px", { lineHeight: "1.5" }],
        "body-sm": ["12px", { lineHeight: "1.4" }],
        code: ["13px", { lineHeight: "1.5", fontFamily: "JetBrains Mono" }],
      },
      borderRadius: {
        DEFAULT: "8px",
        sm: "4px",
        md: "8px",
        lg: "12px",
        xl: "16px",
      },
      boxShadow: {
        card: "0 1px 3px rgba(13, 59, 59, 0.08), 0 1px 2px rgba(13, 59, 59, 0.04)",
        "card-hover": "0 4px 6px rgba(13, 59, 59, 0.1), 0 2px 4px rgba(13, 59, 59, 0.06)",
        modal: "0 20px 25px rgba(13, 59, 59, 0.15), 0 10px 10px rgba(13, 59, 59, 0.04)",
      },
    },
  },
  plugins: [],
};
