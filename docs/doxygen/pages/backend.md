@page backend_overview Backend Overview

The backend is a Spring Boot service responsible for API endpoints, authentication, and
persistence. The code is organized into controllers, services, repositories, and JPA entities.

## REST controllers

Controllers expose HTTP endpoints under `/api`. For example, the comics endpoint allows public
read access and admin creation:

@code{.java}
@GetMapping
public List<Comic> getAll() {
    return comicRepository.findByRedactedFalse();
}

@PostMapping
@ResponseStatus(HttpStatus.CREATED)
@PreAuthorize("hasRole('ADMIN')")
public Comic create(@RequestBody ComicCreateRequest request) {
    Comic comic = new Comic();
    comic.setTitle(request.getTitle());
    comic.setAuthor(request.getAuthor());
    comic.setIsbn(request.getIsbn());
    comic.setDescription(request.getDescription());
    comic.setMainCharacter(request.getMainCharacter());
    comic.setSeries(request.getSeries());
    comic.setPublishedYear(request.getPublishedYear());
    comic.setPrice(request.getPrice());
    comic.setImage(request.getImage());
    comic.setComicType(ComicType.fromValue(request.getComicType()));

    if (request.getCategoryId() != null) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();
        comic.setCategory(category);
    }

    if (request.getConditionId() != null) {
        ComicCondition condition = comicConditionRepository.findById(request.getConditionId()).orElseThrow();
        comic.setCondition(condition);
    }

    return comicRepository.save(comic);
}
@endcode

Source: `bazingaBE/src/main/java/com/bazinga/bazingabe/controller/ComicController.java`.

## Security and authentication

JWT authentication is applied through a filter and method-level authorization rules. The security
configuration disables sessions and permits unauthenticated access to login and public browsing
endpoints:

@code{.java}
http
    .csrf(csrf -> csrf.disable())
    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/comics/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/categories/**", "/api/conditions/**", "/api/news/**")
        .permitAll()
        .anyRequest().authenticated())
    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
@endcode

Source: `bazingaBE/src/main/java/com/bazinga/bazingabe/config/SecurityConfig.java`.

## Persistence layer

Entities map the domain model to relational tables. The `Comic` entity defines basic attributes,
relationships, and lifecycle timestamps:

@code{.java}
@Entity
@Table(name = "comics")
public class Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id")
    private ComicCondition condition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "comic_type", nullable = false, length = 20)
    private ComicType comicType = ComicType.PHYSICAL_COPY;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
@endcode

Source: `bazingaBE/src/main/java/com/bazinga/bazingabe/entity/Comic.java`.
