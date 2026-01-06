import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";

const Library = () => (
  <div className="min-h-screen bg-background">
    <Header />
    <main className="container mx-auto px-4 py-16">
      <div className="max-w-3xl mx-auto text-center space-y-6">
        <p className="text-sm font-semibold uppercase tracking-[0.3em] text-primary">Your Library</p>
        <h1 className="text-4xl font-black tracking-tight text-foreground">Your collection is on the way.</h1>
        <p className="text-muted-foreground">
          We are preparing a personalized library experience for your Bazinga Unlimited and Premium content.
        </p>
        <Link to="/bazinga-unlimited">
          <Button size="lg">Explore Subscriptions</Button>
        </Link>
      </div>
    </main>
    <Footer />
  </div>
);

export default Library;
