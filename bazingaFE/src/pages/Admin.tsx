import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useToast } from "@/components/ui/use-toast";
import { apiFetch } from "@/lib/api";
import { useAuth } from "@/contexts/AuthContext";

type Category = {
  id: number;
  name: string;
};

type Condition = {
  id: number;
  description: string;
};

const initialFormState = {
  title: "",
  author: "",
  isbn: "",
  description: "",
  publishedYear: "",
  conditionId: "",
  categoryId: "",
  price: "",
  image: "",
};

const Admin = () => {
  const { user, token } = useAuth();
  const { toast } = useToast();
  const navigate = useNavigate();
  const [formState, setFormState] = useState(initialFormState);
  const isAdmin = user?.role === "ADMIN";

  const { data: categories = [], isError: categoriesError } = useQuery<Category[]>({
    queryKey: ["categories"],
    queryFn: () => apiFetch<Category[]>("/api/categories"),
  });

  const { data: conditions = [], isError: conditionsError } = useQuery<Condition[]>({
    queryKey: ["conditions"],
    queryFn: () => apiFetch<Condition[]>("/api/conditions"),
  });

  const hasAdminAccess = useMemo(() => Boolean(user && isAdmin), [user, isAdmin]);

  const updateField = (field: keyof typeof formState, value: string) => {
    setFormState((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!token) {
      toast({
        title: "Sign in required",
        description: "Please sign in with an admin account to add comics.",
        variant: "destructive",
      });
      return;
    }

    try {
      await apiFetch("/api/comics", {
        method: "POST",
        authToken: token,
        body: JSON.stringify({
          title: formState.title,
          author: formState.author || null,
          isbn: formState.isbn || null,
          description: formState.description || null,
          publishedYear: formState.publishedYear ? Number(formState.publishedYear) : null,
          conditionId: formState.conditionId ? Number(formState.conditionId) : null,
          categoryId: formState.categoryId ? Number(formState.categoryId) : null,
          price: formState.price ? Number(formState.price) : null,
          image: formState.image || null,
        }),
      });
      toast({
        title: "Comic created",
        description: `${formState.title} has been added to the catalog.`,
      });
      setFormState(initialFormState);
    } catch (error: any) {
      toast({
        title: "Unable to add comic",
        description: error?.message || "Please check the form details and try again.",
        variant: "destructive",
      });
    }
  };

  if (!user) {
    return (
      <div className="min-h-screen bg-background flex flex-col">
        <Header />
        <main className="container mx-auto px-4 py-16 flex-1">
          <Card className="max-w-2xl mx-auto">
            <CardHeader>
              <CardTitle>Admin access required</CardTitle>
              <CardDescription>Sign in with an administrator account to manage comics.</CardDescription>
            </CardHeader>
            <CardContent>
              <Button onClick={() => navigate("/auth")}>Go to sign in</Button>
            </CardContent>
          </Card>
        </main>
        <Footer />
      </div>
    );
  }

  if (!hasAdminAccess) {
    return (
      <div className="min-h-screen bg-background flex flex-col">
        <Header />
        <main className="container mx-auto px-4 py-16 flex-1">
          <Card className="max-w-2xl mx-auto border-destructive/40">
            <CardHeader>
              <CardTitle>Access denied</CardTitle>
              <CardDescription>Only ADMIN users can add new comics.</CardDescription>
            </CardHeader>
            <CardContent className="flex flex-wrap gap-3">
              <Button variant="outline" onClick={() => navigate("/")}>
                Return to storefront
              </Button>
              <Button onClick={() => navigate("/auth")}>Switch account</Button>
            </CardContent>
          </Card>
        </main>
        <Footer />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <Header />
      <main className="container mx-auto px-4 py-12 flex-1">
        <div className="max-w-5xl mx-auto space-y-8">
          <div>
            <p className="text-sm text-primary font-semibold tracking-wide uppercase">Admin console</p>
            <h1 className="text-3xl md:text-4xl font-black text-foreground mt-2">Add a new comic</h1>
            <p className="text-muted-foreground mt-3 max-w-2xl">
              Fill in the catalog details below. The form mirrors the backend comic entity, including condition,
              category, and pricing metadata.
            </p>
          </div>

          <Card className="shadow-lg border-muted">
            <CardHeader>
              <CardTitle>Comic details</CardTitle>
              <CardDescription>Provide the information customers will see in the storefront.</CardDescription>
            </CardHeader>
            <CardContent>
              <form className="space-y-6" onSubmit={handleSubmit}>
                <div className="grid gap-6 md:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="title">Title</Label>
                    <Input
                      id="title"
                      placeholder="Amazing Fantasy #15"
                      value={formState.title}
                      onChange={(event) => updateField("title", event.target.value)}
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="author">Author</Label>
                    <Input
                      id="author"
                      placeholder="Stan Lee"
                      value={formState.author}
                      onChange={(event) => updateField("author", event.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="isbn">ISBN</Label>
                    <Input
                      id="isbn"
                      placeholder="978-1302926735"
                      value={formState.isbn}
                      onChange={(event) => updateField("isbn", event.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="publishedYear">Published year</Label>
                    <Input
                      id="publishedYear"
                      type="number"
                      placeholder="1962"
                      min="1900"
                      max="2100"
                      value={formState.publishedYear}
                      onChange={(event) => updateField("publishedYear", event.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="price">Price</Label>
                    <Input
                      id="price"
                      type="number"
                      min="0"
                      step="0.01"
                      placeholder="14.99"
                      value={formState.price}
                      onChange={(event) => updateField("price", event.target.value)}
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="image">Cover image URL</Label>
                    <Input
                      id="image"
                      placeholder="https://..."
                      value={formState.image}
                      onChange={(event) => updateField("image", event.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="categoryId">Category</Label>
                    <Select
                      value={formState.categoryId}
                      onValueChange={(value) => updateField("categoryId", value)}
                    >
                      <SelectTrigger id="categoryId">
                        <SelectValue placeholder="Select a category" />
                      </SelectTrigger>
                      <SelectContent>
                        {categories.map((category) => (
                          <SelectItem key={category.id} value={String(category.id)}>
                            {category.name}
                          </SelectItem>
                        ))}
                        {!categories.length && (
                          <SelectItem value="none" disabled>
                            {categoriesError ? "Unable to load categories" : "No categories available"}
                          </SelectItem>
                        )}
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="conditionId">Condition</Label>
                    <Select
                      value={formState.conditionId}
                      onValueChange={(value) => updateField("conditionId", value)}
                    >
                      <SelectTrigger id="conditionId">
                        <SelectValue placeholder="Select condition" />
                      </SelectTrigger>
                      <SelectContent>
                        {conditions.map((condition) => (
                          <SelectItem key={condition.id} value={String(condition.id)}>
                            {condition.description}
                          </SelectItem>
                        ))}
                        {!conditions.length && (
                          <SelectItem value="none" disabled>
                            {conditionsError ? "Unable to load conditions" : "No conditions available"}
                          </SelectItem>
                        )}
                      </SelectContent>
                    </Select>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="description">Description</Label>
                  <Textarea
                    id="description"
                    placeholder="Short description for the storefront..."
                    rows={5}
                    value={formState.description}
                    onChange={(event) => updateField("description", event.target.value)}
                  />
                </div>

                <div className="flex flex-wrap items-center justify-between gap-3">
                  <p className="text-xs text-muted-foreground">
                    Only administrators can submit this form. Pricing should match the backend precision.
                  </p>
                  <Button type="submit" className="px-8">
                    Add comic
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default Admin;
