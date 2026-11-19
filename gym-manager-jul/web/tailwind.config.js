/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
    '../../basilisk-libs/packages/ui/src/**/*.{js,ts,jsx,tsx}',
    '../../basilisk-libs/packages/auth/src/**/*.{js,ts,jsx,tsx}',
    '../../basilisk-libs/packages/shell/src/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f0fdfd',
          100: '#d9f2f2',
          200: '#b3e5e5',
          300: '#7ccfcf',
          400: '#5BA3A0',
          500: '#3A8585',
          600: '#2d6b6b',
          700: '#1E5C5C',
          800: '#154a4a',
          900: '#0D3B3B',
        },
      },
      boxShadow: {
        card: '0 1px 3px 0 rgb(0 0 0 / 0.06), 0 1px 2px -1px rgb(0 0 0 / 0.06)',
      },
    },
  },
  plugins: [],
}
