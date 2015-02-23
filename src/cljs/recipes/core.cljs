(ns recipes.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [ajax.core :refer [POST GET DELETE]]
              [reagent-forms.core :refer [bind-fields]])
    (:import goog.History))

;; -------------------------
;; State Atom

(def state (atom {:saved? false}))

;; -------------------------
;; SET / GET

(defn set-value! [id value]
  (swap! state assoc :saved? false)
  (swap! state assoc-in [:doc id] value))

(defn get-value [id]
  (get-in @state [:doc id]))

;; -------------------------
;; Declarations

(def new-recipe (atom {:name "" :avatar_url "" :instructions ""}))

(def ingredients (atom (sorted-map)))

(def counter (atom 0))

(def recipes (atom []))

;; -------------------------
;; Forms Helpers

;; Recipes

(defn row [label & body]
  [:div.row
    [:div.col-xs-2 [:span label]]
    [:div.col-xs-6 body]])

(defn recipe-name-input [label]
  [row label [:input.form-control {:field :text :on-change #(swap! new-recipe assoc-in [:name] (-> % .-target .-value))}]])

(defn recipe-avatar-url [label]
  [row label [:input.form-control {:field :text :on-change #(swap! new-recipe assoc-in [:avatar_url] (-> % .-target .-value))}]])

(defn recipe-instructions-input [label]
  [row label [:textarea.form-control {:rows 4 :on-change #(swap! new-recipe assoc-in [:instructions] (-> % .-target .-value))}]])

;; Ingredients

(defn ingredient-row [label name & quantity & unit]
  [:div.row
    [:div.col-xs-2 label]
    [:div.col-xs-3 name]
    [:div.col-xs-2 quantity]
    [:div.col-xs-1 unit]])

(defn ingredient-name-input [id]
  [:input.form-control {:field :text :id id :on-change #(swap! ingredients assoc-in [id :name] (-> % .-target .-value))}])

(defn ingredient-quantity-input [id]
  [:input.form-control {:type :number :id id :on-change #(swap! ingredients assoc-in [id :quantity] (-> % .-target .-value))}])

(defn ingredient-unit-input [id]
  [:select.form-control {:field :list :id id :on-change #(swap! ingredients assoc-in [id :units] (-> % .-target .-value))}
    [:option "Kg/s"]
    [:option "Cup/s"]
    [:option "Spoon/s"]])

(defn ingredient-inputs [id label]
 [ingredient-row label [ingredient-name-input id] [ingredient-quantity-input id] [ingredient-unit-input id]])

;; -------------------------
;; Ajax Methods

(defn save-doc []
  (POST (str js/context "/recipes/new")
    {:params {:recipe @new-recipe
              :ingredients @ingredients}
    :handler (fn [_] (secretary/dispatch! "#/"))}))

(defn get-recipes []
  (GET (str js/context "/recipes")
    :handler (fn [response] (reset! recipes response))
    :response-format :json
    :keywords? true))

(defn destroy-recipe [recipe-id]
  (DELETE (str js/context "/recipes/" recipe-id)
    :handler (fn [_] (get-recipes))))

;; -------------------------
;; Home

(defn recipes-table-header []
  [:tr
    [:td [:b "Avatar"]]
    [:td [:b "Recipe Name"]]
    [:td [:b "Actions"]]])

(defn recipe-actions []
  [:div
    ])

(defn recipe-item [recipe]
  (let [recipe-name (get recipe :name)
        recipe-avatar-url (get recipe :avatar_url)
        recipe-id (get recipe :id)]
    [:tr
      [:td [:img {:src recipe-avatar-url :class "img-thumbnail" :style {:max-width "150px"}}]]
      [:td recipe-name]
      [:td
        [:button "Show"]
        [:button "Edit"]
        [:button {:onClick #(destroy-recipe recipe-id)} "Destroy"]]]))

(defn home-page []
  (get-recipes)
  [:div
    [:div.row [:h2 "Recipes"]]
    [:table.table.table-hover
      [recipes-table-header]
      (for [recipe (filter identity @recipes)]
        (do
          [recipe-item recipe]))]
    [:br]
    [:div.row [:button { :class "btn-primary btn-lg" :onClick #(secretary/dispatch! "#/new_recipe")} "New Recipe"]]])

;; -------------------------
;; About

(defn about-page []
  [:div [:h2 "About Recipes"]
   [:div "Made by Maikol!!"]])

;; -------------------------
;; New Recipe

(defn add-ingredient [text]
  (let [id (swap! counter inc)]
    (swap! ingredients assoc id {:id id :name "" :units "Kg/s" :quantity 0})))

(defn ingredient-item []
  (fn [{:keys [id]}]
    [ingredient-inputs id "Item"]))

(defn new-recipe-add-ingredient-button []
  [:div
    [:button {:type "submit"
              :class "btn btn-default btn-small"
              :onClick #(add-ingredient "item")}
      "Add Ingredient"]])

(defn new-recipe-ingredient-list []
  (let [items (vals @ingredients)]
    [:div
      (for [ingredient (filter identity items)]
        ^{:key (:id ingredient)} [ingredient-item ingredient])]))

(defn new-recipe-name []
  [:div
    [recipe-name-input "Recipe Name"]])

(defn new-recipe-avatar-url []
  [:div
    [recipe-avatar-url "Avatar URL"]])

(defn new-recipe-instructions []
  [:div
    [recipe-instructions-input "Instructions"]])

(defn new-recipe-submit-button []
  [:button {:type "submit"
            :class "btn btn-default"
            :onClick save-doc}
    "Submit"])

(defn new-recipe-body []
  [:div
    [:section
      [:ul
        [new-recipe-name]]
      [:ul
        [new-recipe-avatar-url]]
      [:ul
        [new-recipe-ingredient-list]]
      [:ul
        [new-recipe-add-ingredient-button]]
      [:ul
        [new-recipe-instructions]]
      [:ul
        [new-recipe-submit-button]]]])

(defn new-recipe-page []
  [:div
    [:section
      [:header
        [:h1 "New Recipe"]]
      [new-recipe-body]]])

;; -------------------------
;; Nav Bar

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand {:href "#/"} "Recipe"]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav
      [:li {:class (when (= home-page (:page @state)) "active")}
       [:a {:on-click #(secretary/dispatch! "#/")} "Home"]]
      [:li {:class (when (= about-page (:page @state)) "active")}
       [:a {:on-click #(secretary/dispatch! "#/about")} "About"]]]]]])

;; -------------------------
;; Pages methods

(defn page []
  [(:page @state)])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page)
  (swap! state assoc :page home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page)
  (swap! state assoc :page about-page))

(secretary/defroute "/new_recipe" []
  (session/put! :current-page #'new-recipe-page)
  (swap! state assoc :page new-recipe-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn init! []
  (hook-browser-navigation!)
  (reagent/render-component [current-page] (.getElementById js/document "app"))
  (reagent/render-component [navbar]       (.getElementById js/document "navbar")))
