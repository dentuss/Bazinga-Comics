import { useState, useMemo } from "react";
import { useSearchParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import Header from "@/components/Header";
import HeroCarousel from "@/components/HeroCarousel";
import ComicSection from "@/components/ComicSection";
import UnlimitedBanner from "@/components/UnlimitedBanner";
import Footer from "@/components/Footer";
import FilterBar from "@/components/FilterBar";
import BrowseByFilter from "@/components/BrowseByFilter";
import ComicModal from "@/components/ComicModal";
import { Button } from "@/components/ui/button";
import { apiFetch } from "@/lib/api";

export interface ComicDto {
  id: number;
  title: string;
  author?: string;
  description?: string;
  image: string;
  price: number;
  category?: { name: string };
}

const Index = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [selectedComic, setSelectedComic] = useState<any>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [browseFilter, setBrowseFilter] = useState<{ type: string; value: string }>({ type: "", value: "" });

  const { data: comics = [] } = useQuery<ComicDto[]>({
    queryKey: ["comics"],
    queryFn: () => apiFetch<ComicDto[]>("/api/comics"),
  });

  const searchQuery = searchParams.get("search") || "";
  const viewAll = searchParams.get("view") === "all";

  const handleComicClick = (comic: any) => {
    setSelectedComic(comic);
    setIsModalOpen(true);
  };

  const handleBrowseFilterChange = (type: string, value: string) => {
    setBrowseFilter({ type, value });
  };

  const clearFilters = () => {
    setSearchParams({});
    setBrowseFilter({ type: "", value: "" });
  };

  const allComics = comics.map((comic) => ({
    ...comic,
    creators: comic.author || "",
    rating: 4.5,
    series: comic.category?.name || "Series",
    character: comic.author?.split(",")[0] || "",
  }));

  const filteredComics = useMemo(() => {
    let comics = [...allComics];

    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      comics = comics.filter(
        (comic) =>
          comic.title.toLowerCase().includes(query) ||
          comic.creators.toLowerCase().includes(query) ||
          comic.series.toLowerCase().includes(query) ||
          comic.character.toLowerCase().includes(query)
      );
    }

    if (browseFilter.value && !browseFilter.value.startsWith("All")) {
      if (browseFilter.type === "series") {
        comics = comics.filter((comic) => comic.series === browseFilter.value);
      } else if (browseFilter.type === "character") {
        comics = comics.filter((comic) => comic.character === browseFilter.value);
      } else if (browseFilter.type === "creator") {
        comics = comics.filter((comic) =>
          comic.creators.toLowerCase().includes(browseFilter.value.split(" ").pop()?.toLowerCase() || "")
        );
      }
    }

    return comics;
  }, [searchQuery, browseFilter, allComics]);

  const isFiltered = searchQuery || browseFilter.value || viewAll;

  const newThisWeek = allComics.slice(0, 12);
  const bestSelling = allComics.slice(4, 10);
  const readForFree = allComics.slice(10, 16);

  return (
    <div className="min-h-screen bg-background">
      <Header />
      <FilterBar />
      <main>
        <HeroCarousel />
        <BrowseByFilter onFilterChange={handleBrowseFilterChange} />
        
        {isFiltered ? (
          <section className="container mx-auto px-4 py-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-black text-foreground">
                {searchQuery ? `SEARCH RESULTS FOR "${searchQuery.toUpperCase()}"` : viewAll ? "ALL COMICS" : "FILTERED RESULTS"}
                <span className="text-muted-foreground text-lg font-normal ml-2">({filteredComics.length} comics)</span>
              </h2>
              <Button variant="outline" onClick={clearFilters}>
                Clear Filters
              </Button>
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
              {filteredComics.map((comic, index) => (
                <div
                  key={index}
                  onClick={() => handleComicClick(comic)}
                  className="cursor-pointer group"
                >
                  <div className="relative overflow-hidden rounded-lg shadow-lg transition-transform duration-300 group-hover:-translate-y-2">
                    <img
                      src={comic.image}
                      alt={comic.title}
                      className="w-full aspect-[2/3] object-cover"
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
                  </div>
                  <h3 className="mt-2 text-xs font-bold text-foreground line-clamp-2 group-hover:text-primary transition-colors">
                    {comic.title}
                  </h3>
                  <p className="text-xs text-muted-foreground">{comic.creators}</p>
                </div>
              ))}
            </div>
            {filteredComics.length === 0 && (
              <div className="text-center py-12">
                <p className="text-muted-foreground text-lg">No comics found matching your criteria.</p>
                <Button variant="link" onClick={clearFilters} className="mt-2">
                  Clear all filters
                </Button>
              </div>
            )}
          </section>
        ) : (
          <>
            <ComicSection 
              id="new-this-week" 
              title="NEW THIS WEEK" 
              comics={newThisWeek} 
              onComicClick={handleComicClick}
            />
            <ComicSection 
              id="best-selling" 
              title="BEST SELLING DIGITAL COMICS" 
              comics={bestSelling}
              onComicClick={handleComicClick}
            />
            <UnlimitedBanner />
            <ComicSection 
              id="read-for-free" 
              title="READ FOR FREE" 
              comics={readForFree}
              onComicClick={handleComicClick}
            />
          </>
        )}
      </main>
      <Footer />
      
      {selectedComic && (
        <ComicModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          comic={selectedComic}
        />
      )}
    </div>
  );
};

export default Index;