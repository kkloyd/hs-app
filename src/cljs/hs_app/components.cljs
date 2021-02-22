(ns hs-app.components
  (:require
   [reagent.core :as r]
   [accountant.core :as accountant]
   [hs-app.util :refer [gender-str]]
   [hs-app.router :refer [path-for]]
   [hs-app.api :refer [create-patient! edit-patient! delete-patient!]]))


(defn log [object] (.dir js/console (clj->js object)))


;; Patients List
(defn delete-handler [id fullname]
  (let [confirm? (js/confirm (str "Удалить пациента с id " id " и именем " fullname " ?"))]
    (if (= confirm? true) (delete-patient! id)
        :else)))

(defn patients-list [patients]
  [:ul.patients-list
   [:div.labels
    [:div.patient-item {:style {:color "gray"}}
     [:span "ФИО"]
     [:span "Пол"]
     [:span "Дата рождения"]
     [:span "Адрес"]
     [:span "Номер полиса ОМС"]]]
   (map (fn [{:keys [id fullname gender birth_date address policy_number]}]
          [:li {:name id :key id}
           [:div.patient-item
            [:span fullname]
            [:span (gender-str gender)]
            [:span birth_date]
            [:span address]
            [:span policy_number]]
           [:div {:style {:margin-top "20px"}}
            [:a {:href (path-for :edit-page {:id id})}
             [:button.btn.btn-success {:style {:margin-right "5px"}}
              "Редактировать"]]
            [:button.btn.btn-danger
             {:on-click #(delete-handler id fullname)}
             "Удалить"]]])
        patients)])


;; Patients Form

(defn form-item [label input]
  [:div.form-item
   [:label label]
   input])

(defn patients-form [data]
  (let [status-ok (r/atom nil)
        f (r/atom (:patient @data))
        form-copy (atom (:patient @data))
        id (:id @f)]
    (fn []
      (cond (= @status-ok true) (do (accountant/navigate! (path-for :index)) (js/alert "Сохранено") (reset! status-ok nil))
            (= @status-ok false) (do (js/alert "Не удалось") (reset! status-ok nil)))
      [:div.patients-form
       (form-item "ФИО" [:input {:type :text :value (:fullname @f) :size 100
                                 :on-change #(swap! f assoc :fullname (-> % .-target .-value))}])
       (form-item "Пол" [:select {:type :text :value (:gender @f)
                                  :on-change #(swap! f assoc :gender (js/parseInt (-> % .-target .-value)))}
                         [:option {:value -1} ""]
                         [:option {:value 1} "М"]
                         [:option {:value 0} "Ж"]])

       (form-item "Дата рождения" [:input {:type :date :value (:birth_date @f) :size 100
                                           :on-change #(swap! f assoc :birth_date (-> % .-target .-value))}])


       (form-item "Адрес"  [:input {:type :text :value (:address @f) :size 100
                                    :on-change (fn [e]
                                                 (let [val (-> e .-target .-value)]
                                                   (swap! f assoc :address val)))}])


       (form-item "Номер полиса ОМС" [:input {:type :number :value (:policy_number @f)
                                              :required true
                                              :on-change #(swap! f assoc :policy_number  (js/parseInt (-> % .-target .-value)))}])


       (if (nil? id)
         [:button.btn.btn-primary
          {:on-click #(create-patient! @f status-ok)}
          "Сохранить"]

         [:button.btn.btn-success
          {:on-click #(edit-patient! id (dissoc @f :id) status-ok)}
          "Редактировать"])])))
