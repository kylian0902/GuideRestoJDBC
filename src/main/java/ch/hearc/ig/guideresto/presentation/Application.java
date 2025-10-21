package ch.hearc.ig.guideresto.presentation;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.mappers.*;

import java.sql.SQLException;
import java.util.*;

public class Application {

    // === Mappers (remplacent FakeItems) ===
    private final CityMapper cityMapper = new CityMapper();
    private final RestaurantTypeMapper typeMapper = new RestaurantTypeMapper();
    private final EvaluationCriteriaMapper criteriaMapper = new EvaluationCriteriaMapper();
    private final RestaurantMapper restaurantMapper = new RestaurantMapper();
    private final BasicEvaluationMapper basicEvalMapper = new BasicEvaluationMapper();          // LIKES
    private final CompleteEvaluationMapper completeEvalMapper = new CompleteEvaluationMapper(); // COMMENTAIRES
    private final GradeMapper gradeMapper = new GradeMapper();                                  // NOTES

    private final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            new Application().run();
        } catch (Exception e) {
            System.err.println("Erreur fatale: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void run() throws Exception {
        int choice;
        do {
            menu();
            choice = readInt("Votre choix: ");
            switch (choice) {
                case 1 -> listRestaurants();
                case 2 -> showRestaurantDetails();
                case 3 -> addLike();
                case 4 -> addCompleteEvaluation();
                case 0 -> System.out.println("Au revoir 👋");
                default -> System.out.println("Choix invalide.");
            }
        } while (choice != 0);
    }

    private void menu() {
        System.out.println();
        System.out.println("===== GuideResto — Menu =====");
        System.out.println("1) Lister les restaurants");
        System.out.println("2) Voir détails d'un restaurant");
        System.out.println("3) Ajouter un LIKE");
        System.out.println("4) Ajouter une évaluation complète");
        System.out.println("0) Quitter");
        System.out.println("=============================");
    }

    // ----------------------------------------------------------------------
    // 1) Lister restaurants (remplace FakeItems.getAllRestaurants())
    // ----------------------------------------------------------------------
    private void listRestaurants() {
        try {
            List<Restaurant> restos = restaurantMapper.findAll();
            if (restos.isEmpty()) {
                System.out.println("(aucun restaurant)");
                return;
            }
            System.out.println("\n-- Restaurants --");
            for (Restaurant r : restos) {
                String typeLabel = (r.getType() != null) ? safe(r.getType().getLabel()) : "?";
                String cityName = "?";
                if (r.getAddress() != null && r.getAddress().getCity() != null) {
                    cityName = safe(r.getAddress().getCity().getCityName());
                }
                System.out.printf("#%d  %s  [Type: %s | Ville: %s]%n",
                        nz(r.getId()), safe(r.getName()), typeLabel, cityName);
            }
        } catch (SQLException e) {
            error("Impossible de lister les restaurants", e);
        }
    }

    // ----------------------------------------------------------------------
    // 2) Détails d’un restaurant
    //    (remplace FakeItems.getLikes(r) / FakeItems.getCompleteEvaluations(r))
    // ----------------------------------------------------------------------
    private void showRestaurantDetails() {
        try {
            int id = readInt("Id du restaurant: ");
            Optional<Restaurant> opt = restaurantMapper.findById(id);
            if (opt.isEmpty()) {
                System.out.println("Restaurant introuvable.");
                return;
            }
            Restaurant r = opt.get();
            System.out.println("\n-- Détails du restaurant --");
            System.out.println("Nom: " + safe(r.getName()));
            System.out.println("Type: " + (r.getType() != null ? safe(r.getType().getLabel()) : "?"));
            if (r.getAddress() != null && r.getAddress().getCity() != null) {
                System.out.println("Ville: " + safe(r.getAddress().getCity().getCityName()));
                System.out.println("Adresse: " + safe(r.getAddress().getStreet()));
            }

            // Likes (LIKES)
            List<BasicEvaluation> likes = basicEvalMapper.findByRestaurant(nz(r.getId()));
            long likesCount = likes.stream().filter(be -> Boolean.TRUE.equals(be.getLikeRestaurant())).count();
            long dislikesCount = likes.size() - likesCount;
            System.out.printf("Likes: %d | Dislikes: %d%n", likesCount, dislikesCount);

            // Évaluations complètes (COMMENTAIRES) + (option) charger les notes (NOTES)
            List<CompleteEvaluation> evals = completeEvalMapper.findByRestaurant(nz(r.getId()));
            if (evals.isEmpty()) {
                System.out.println("(aucune évaluation complète)");
            } else {
                System.out.println("-- Évaluations complètes --");
                for (CompleteEvaluation ev : evals) {
                    String date = (ev.getVisitDate() != null) ? ev.getVisitDate().toString() : "?";
                    System.out.printf("  #%d [%s] par %s : %s%n",
                            nz(ev.getId()), date, safe(ev.getUsername()), safe(ev.getComment()));
                    // si tu veux afficher les notes :
                    // var notes = gradeMapper.findByEvaluation(nz(ev.getId()));
                    // for (Grade g : notes) {
                    //     System.out.printf("    - Critère #%d : %d/10%n",
                    //         nz(g.getCriteria() != null ? g.getCriteria().getId() : null),
                    //         nz(g.getGrade()));
                    // }
                }
            }

        } catch (SQLException e) {
            error("Impossible de charger les détails", e);
        }
    }

    // ----------------------------------------------------------------------
    // 3) Ajouter un LIKE (remplace FakeItems.addLike(...))
    // ----------------------------------------------------------------------
    private void addLike() {
        try {
            int restId = readInt("Id du restaurant: ");
            Optional<Restaurant> opt = restaurantMapper.findById(restId);
            if (opt.isEmpty()) {
                System.out.println("Restaurant introuvable.");
                return;
            }
            Restaurant r = opt.get();

            String val = readString("Like ? (o/n): ").trim().toLowerCase(Locale.ROOT);
            boolean like = val.startsWith("o") || val.startsWith("y");
            String ip = readString("Adresse IP (optionnel): ");

            BasicEvaluation be = new BasicEvaluation();
            be.setRestaurant(r);
            be.setLikeRestaurant(like);
            be.setVisitDate(new Date()); // maintenant
            be.setIpAddress(ip.isBlank() ? null : ip);

            basicEvalMapper.insert(be);
            System.out.println("LIKE ajouté (#" + nz(be.getId()) + ")");
        } catch (SQLException e) {
            error("Impossible d'ajouter le LIKE", e);
        }
    }

    // ----------------------------------------------------------------------
    // 4) Ajouter une évaluation complète (remplace FakeItems.addCompleteEvaluation(...))
    //    Insert transactionnel: COMMENTAIRES + NOTES
    // ----------------------------------------------------------------------
    private void addCompleteEvaluation() {
        try {
            int restId = readInt("Id du restaurant: ");
            Optional<Restaurant> opt = restaurantMapper.findById(restId);
            if (opt.isEmpty()) {
                System.out.println("Restaurant introuvable.");
                return;
            }
            Restaurant r = opt.get();

            String user = readString("Votre nom (affiché): ");
            String comment = readString("Commentaire: ");

            CompleteEvaluation ev = new CompleteEvaluation();
            ev.setRestaurant(r);
            ev.setUsername(user);
            ev.setComment(comment);
            ev.setVisitDate(new Date()); // maintenant
            ev.setGrades(new HashSet<>()); // on peut ajouter des notes ci-dessous

            // (Option) proposer de saisir quelques notes :
            if (yesNo("Ajouter des notes (y/n) ? ")) {
                List<EvaluationCriteria> criterias = criteriaMapper.findAll();
                if (criterias.isEmpty()) {
                    System.out.println("(aucun critère)");
                } else {
                    System.out.println("-- Saisie des notes (0..10, vide pour passer) --");
                    for (EvaluationCriteria c : criterias) {
                        String raw = readString("  " + nz(c.getId()) + " - " + safe(c.getName()) + " : ");
                        if (raw == null || raw.isBlank()) continue;
                        try {
                            int note = Integer.parseInt(raw.trim());
                            if (note < 0 || note > 10) {
                                System.out.println("  Ignoré (hors plage 0..10)");
                                continue;
                            }
                            Grade g = new Grade();
                            g.setCriteria(c);
                            g.setGrade(note);
                            ev.getGrades().add(g);
                        } catch (NumberFormatException nfe) {
                            System.out.println("  Ignoré (non numérique)");
                        }
                    }
                }
            }

            completeEvalMapper.insertWithGrades(ev);
            System.out.println("Évaluation complète ajoutée (#" + nz(ev.getId()) + ")");

        } catch (SQLException e) {
            error("Impossible d'ajouter l'évaluation complète", e);
        }
    }

    // ======================================================================
    // Helpers I/O
    // ======================================================================

    private int readInt(String label) {
        while (true) {
            try {
                System.out.print(label);
                String s = in.nextLine();
                return Integer.parseInt(s.trim());
            } catch (Exception e) {
                System.out.println("Veuillez entrer un nombre.");
            }
        }
    }

    private String readString(String label) {
        System.out.print(label);
        return in.nextLine();
    }

    private boolean yesNo(String label) {
        String s = readString(label).trim().toLowerCase(Locale.ROOT);
        return s.startsWith("y") || s.startsWith("o");
    }

    private static void error(String msg, Exception e) {
        System.out.println(msg);
        if (e != null) {
            System.out.println("    → " + e.getMessage());
        }
    }

    private static String safe(String s) {
        return (s == null) ? "" : s;
    }

    private static int nz(Integer i) {
        return (i == null) ? 0 : i;
    }
}
