@page frontend_overview Frontend Overview

The frontend is a Vite + React application styled with Tailwind CSS. It consumes the backend
API, renders comics, and provides interactive UI components.

## API client utilities

Client requests use a centralized helper that handles headers, bearer tokens, and error
responses:

@code{.ts}
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export async function apiFetch<T>(path: string, options: Options = {}): Promise<T> {
  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  if (options.authToken) {
    headers["Authorization"] = `Bearer ${options.authToken}`;
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Request failed");
  }

  if (response.status === 204) {
    return {} as T;
  }

  return response.json();
}
@endcode

Source: `bazingaFE/src/lib/api.ts`.

## UI components

Components are built as composable React function components. The comic card highlights digital
exclusive items and uses helper utilities to resolve images:

@code{.tsx}
const ComicCard = ({ image, title, creators, comicType, onClick }: ComicCardProps) => {
  const isDigitalExclusive = comicType === "ONLY_DIGITAL";

  return (
    <div
      onClick={onClick}
      className="group relative overflow-hidden rounded-sm bg-card transition-all duration-300 hover:-translate-y-2 hover:shadow-xl hover:shadow-primary/20 cursor-pointer"
    >
      {isDigitalExclusive && (
        <span className="absolute left-2 top-2 z-10 rounded-full bg-yellow-400 px-2 py-1 text-[10px] font-bold uppercase text-black shadow">
          Digital Exclusive
        </span>
      )}
      <div className="aspect-[2/3] overflow-hidden">
        <img
          src={resolveImageUrl(image)}
          alt={title}
          className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-110"
        />
      </div>
      <div className="p-3 space-y-1">
        <h3 className="font-bold text-sm line-clamp-2 group-hover:text-primary transition-colors">
          {title}
        </h3>
        {creators && (
          <p className="text-xs text-muted-foreground line-clamp-1">{creators}</p>
        )}
      </div>
    </div>
  );
};
@endcode

Source: `bazingaFE/src/components/ComicCard.tsx`.
