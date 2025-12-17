import { useState, useMemo } from "react";
import { useSearchParams } from "react-router-dom";
import Header from "@/components/Header";
import HeroCarousel from "@/components/HeroCarousel";
import ComicSection from "@/components/ComicSection";
import UnlimitedBanner from "@/components/UnlimitedBanner";
import Footer from "@/components/Footer";
import FilterBar from "@/components/FilterBar";
import BrowseByFilter from "@/components/BrowseByFilter";
import ComicModal from "@/components/ComicModal";
import { Button } from "@/components/ui/button";

import comic1 from "@/assets/comic-1.jpg";
import comic2 from "@/assets/comic-2.jpg";
import comic3 from "@/assets/comic-3.jpg";
import comic4 from "@/assets/comic-4.jpg";
import comic5 from "@/assets/comic-5.jpg";
import comic6 from "@/assets/comic-6.jpg";
import comic7 from "@/assets/comic-7.jpg";
import comic8 from "@/assets/comic-8.jpg";

const Index = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [selectedComic, setSelectedComic] = useState<any>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [browseFilter, setBrowseFilter] = useState<{ type: string; value: string }>({ type: "", value: "" });

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

  const allComics = [
    { image: comic1, title: "X-MEN: AGE OF MYTH ACTION FIGURE (2025) #1", creators: "Kindt, Unzueta", rating: 4.5, series: "X-Men", character: "Wolverine" },
    { image: comic2, title: "EMPIRES OF VIOLENCE (2025) #1", creators: "Remender, Kim", rating: 4.8, series: "Avengers", character: "Iron Man" },
    { image: comic3, title: "SPIDER-MAN NOIR (2025) #1", creators: "Grayson, Mandrake", rating: 4.7, series: "Spider-Man", character: "Spider-Man" },
    { image: comic4, title: "X-MEN: THE UNBROKEN (2025) #1", creators: "MacKay, Noto", rating: 4.6, series: "X-Men", character: "Scarlet Witch" },
    { image: comic5, title: "WHAT'S THOSE SHADOW (2025) #1", creators: "Hickman, Garbett", rating: 4.4, series: "Avengers", character: "Doctor Strange" },
    { image: comic6, title: "BAZINGA-BOT: SPIDER WATCHBEARABLE EDITION (2025) #1", creators: "Slott, Harries", rating: 4.5, series: "Spider-Man", character: "Spider-Man" },
    { image: comic7, title: "NEW AVENGERS (2025) #1", creators: "Ahmed, Mora", rating: 4.9, series: "Avengers", character: "Captain America" },
    { image: comic8, title: "STAR WARS (2025) #14", creators: "Soule, Unzueta", rating: 4.3, series: "Star Wars", character: "Hulk" },
    { image: comic1, title: "RED HULK (2025) #1", creators: "Parker, Stegman", rating: 4.7, series: "Avengers", character: "Hulk" },
    { image: comic2, title: "ULTIMATE WOLVERINE (2025) #1", creators: "Hickman, Checchetto", rating: 4.8, series: "X-Men", character: "Wolverine" },
    { image: comic3, title: "DEADPOOL (2025) #1", creators: "Duggan, Messina", rating: 4.6, series: "Deadpool", character: "Deadpool" },
    { image: comic4, title: "THE AMAZING SPIDER (2025) #35", creators: "Wells, Romita", rating: 4.5, series: "Spider-Man", character: "Spider-Man" },
    { image: comic5, title: "MOON KNIGHT: FIST OF KHONSHU (2025)", creators: "Mackay, Cappuccio", rating: 5.0, series: "Moon Knight", character: "Doctor Strange" },
    { image: comic6, title: "CAPTAIN AMERICA (2025) #1", creators: "Thompson, Lee", rating: 4.9, series: "Captain America", character: "Captain America" },
    { image: comic7, title: "MIRACLEMAN (2025)", creators: "Gaiman, Buckingham", rating: 4.8, series: "Avengers", character: "Thor" },
    { image: comic8, title: "THE VITALS: TRUE BAD STORIES (2021)", creators: "Ewing, Schiti", rating: 4.7, series: "Venom", character: "Black Widow" },
    { image: comic1, title: "MAJOR X (2019)", creators: "Liefeld", rating: 4.4, series: "X-Men", character: "Wolverine" },
    { image: comic2, title: "VENOM (2025) #1", creators: "Ewing, Hitch", rating: 4.8, series: "Venom", character: "Hulk" },
    { image: comic3, title: "CHEE!NTH (2023) #1", creators: "Buccellato, Berry", rating: 4.2, series: "Spider-Man", character: "Spider-Man" },
    { image: comic4, title: "S.O.D.S. FIRST LOOK INFINITY COMIC (2025) #1", creators: "Cantwell, Ferreira", rating: 4.5, series: "Avengers", character: "Iron Man" },
    { image: comic5, title: "MARVEL'S SPIDER-MAN 2 (2023) #1", creators: "Salazar, Barela, Niscola", rating: 4.6, series: "Spider-Man", character: "Spider-Man" },
    { image: comic6, title: "9/11 20TH ANNIVERSARY TRIBUTE: THE FOUR FIVES (2021) #1", creators: "Various", rating: 4.9, series: "Avengers", character: "Captain America" },
    { image: comic7, title: "THE VITALS: TRUE BAD STORIES (2021)", creators: "Howe, Noto", rating: 4.7, series: "X-Men", character: "Scarlet Witch" },
    { image: comic8, title: "ULTIMATE SPIDER-MAN (2024) #10", creators: "Hickman, Checchetto", rating: 4.8, series: "Spider-Man", character: "Spider-Man" },
  ];

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
  }, [searchQuery, browseFilter]);

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