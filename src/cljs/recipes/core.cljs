(ns recipes.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [ajax.core :refer [POST GET]])
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

(def recipe-name (atom ""))

(def recipe-instructions (atom ""))

(def recipe-avatar-url (atom ""))

(def ingredients (atom (sorted-map)))

(def counter (atom 0))

(def recipes (atom []))

;; -------------------------
;; Forms Helpers

(defn row [label & body]
  [:div.row
    [:div.col-xs-3 [:span label]]
    [:div.col-xs-6 body]])

(defn recipe-name-input [label]
  [row label [:input.form-control {:field :text :value @recipe-name :on-change #(reset! recipe-name (-> % .-target .-value))}]])

(defn ingredient-name-input [id label]
  [row label [:input.form-control {:field :text :id id :on-change #(swap! ingredients assoc-in [id :name] (-> % .-target .-value))}]])

;; -------------------------
;; Ajax Methods

(defn save-doc []
  (POST (str js/context "/recipes/new")
    {:params {:name @recipe-name
              :instructions @recipe-instructions
              :avatar_url @recipe-avatar-url
              :ingredients @ingredients}
    :handler (fn [_] (secretary/dispatch! "#/"))}))

(defn get-recipes []
  (GET (str js/context "/recipes")
    :handler (fn [response] (reset! recipes response))
    :response-format :json
    :keywords? true))

;; -------------------------
;; Home

(defn recipe-item [recipe]
  (let [recipe-name (get recipe :name)
        ingredients-items (get recipe :ingredients)]
    [:li recipe-name]))

(defn home-page []
  (get-recipes)
  [:div
    [:div.row [:h2 "Recipes Index"]]
    [:div.row [:button { :class "btn-warning btn-sm" :onClick #(get-recipes)} "Load Recipes"]]
    [:div
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
    (swap! ingredients assoc id {:id id :name "" :units "cups" :quantity 2})))

(defn ingredient-item []
  (fn [{:keys [id]}]
    [ingredient-name-input id "Item"]))

(defn add-ingredient-button []
  [:div
    [:button {:type "submit"
              :class "btn btn-default btn-small"
              :onClick #(add-ingredient "item")}
      "Add Ingredient"]])

(defn ingredient-list []
  (let [items (vals @ingredients)]
    [:div
      (for [ingredient (filter identity items)]
        ^{:key (:id ingredient)} [ingredient-item ingredient])]))

(defn new-recipe-form []
  [:div
    [recipe-name-input "Recipe Name"]])

(defn submit-recipe-button []
  [:button {:type "submit"
            :class "btn btn-default"
            :onClick save-doc}
    "Submit"])

(defn new-recipe-body []
  [:div
    [:section
      [:ul
        [new-recipe-form]]
      [:ul
        [ingredient-list]]
      [:ul
        [add-ingredient-button]]
      [:ul
        [submit-recipe-button]]]])

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
