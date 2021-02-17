(ns hs-app.home)

(defn home-page []
  (fn [path-for]
    [:div.main
     [:div.main__title
      [:h1 "Список пациентов"]
      [:button.btn.btn-primary {:style {:margin-left "20px"}} "Добавить"]]
     [:ul.patients-list
      (map (fn [patient-id]
             [:li {:name (str "item-" patient-id) :key (str "item-" patient-id)}
              [:a {:href (path-for :details {:id patient-id})} "Пациент: " patient-id]])
           (range 1 10))]]))

