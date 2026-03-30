import { cn } from "@basilisk/utils";

export interface Column<T> {
  /** Chave do objeto ou função de acesso ao valor */
  accessor: keyof T | ((row: T) => React.ReactNode);
  /** Cabeçalho da coluna */
  header: string;
  /** Largura da coluna (CSS) */
  width?: string;
  /** Alinhamento do conteúdo */
  align?: "left" | "center" | "right";
  /** Permite ordenação por esta coluna */
  sortable?: boolean;
}

interface DataTableProps<T> {
  /** Definição das colunas */
  columns: Column<T>[];
  /** Dados a exibir */
  data: T[];
  /** Chave única para cada linha */
  rowKey: keyof T | ((row: T) => string);
  /** Exibe estado de carregamento */
  isLoading?: boolean;
  /** Mensagem exibida quando não há dados */
  emptyMessage?: string;
  /** Callback ao clicar em uma linha */
  onRowClick?: (row: T) => void;
  /** Classes CSS adicionais */
  className?: string;
}

function getCellValue<T>(row: T, accessor: Column<T>["accessor"]): React.ReactNode {
  if (typeof accessor === "function") return accessor(row);
  return row[accessor] as React.ReactNode;
}

function getRowKey<T>(row: T, rowKey: DataTableProps<T>["rowKey"]): string {
  if (typeof rowKey === "function") return rowKey(row);
  return String(row[rowKey]);
}

export function DataTable<T>({
  columns,
  data,
  rowKey,
  isLoading,
  emptyMessage = "Nenhum dado encontrado.",
  onRowClick,
  className,
}: DataTableProps<T>) {
  return (
    <div className={cn("w-full overflow-x-auto rounded-xl border border-gray-100 shadow-card", className)}>
      <table className="w-full border-collapse text-sm">
        <thead>
          <tr className="bg-[#EAF2F2]">
            {columns.map((col) => (
              <th
                key={col.header}
                style={{ width: col.width }}
                className={cn(
                  "px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-[#1E5C5C]",
                  col.align === "center" && "text-center",
                  col.align === "right" && "text-right"
                )}
              >
                {col.header}
              </th>
            ))}
          </tr>
        </thead>

        <tbody className="divide-y divide-gray-50 bg-white">
          {isLoading ? (
            Array.from({ length: 5 }).map((_, i) => (
              <tr key={i}>
                {columns.map((col) => (
                  <td key={col.header} className="px-4 py-3">
                    <div className="h-4 animate-pulse rounded bg-gray-100" />
                  </td>
                ))}
              </tr>
            ))
          ) : data.length === 0 ? (
            <tr>
              <td
                colSpan={columns.length}
                className="px-4 py-10 text-center text-[#8B8B78]"
              >
                {emptyMessage}
              </td>
            </tr>
          ) : (
            data.map((row) => (
              <tr
                key={getRowKey(row, rowKey)}
                onClick={onRowClick ? () => onRowClick(row) : undefined}
                className={cn(
                  "transition-colors duration-100",
                  onRowClick && "cursor-pointer hover:bg-[#EAF2F2]/50"
                )}
              >
                {columns.map((col) => (
                  <td
                    key={col.header}
                    className={cn(
                      "px-4 py-3 text-[#0D3B3B]",
                      col.align === "center" && "text-center",
                      col.align === "right" && "text-right"
                    )}
                  >
                    {getCellValue(row, col.accessor)}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
