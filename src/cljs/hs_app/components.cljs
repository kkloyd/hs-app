(ns hs-app.components
  (:require
   [reagent.core :as r]
   [accountant.core :as accountant]
   [reagent-material-ui.core.snackbar :refer [snackbar]]
   [reagent-material-ui.lab.alert :refer [alert]]
   [hs-app.util :refer [gender-str]]
   [hs-app.router :refer [path-for]]
   [hs-app.states :refer [redirect-to-list? message]]
   [hs-app.api :refer [create-patient! edit-patient! delete-patient!]]))


(defn message-popup []
  (let [success (= true (:status-ok? @message))
        fail (= false (:status-ok? @message))]

    [snackbar {:anchor-origin {:vertical "top" :horizontal "center"}
               :open (:show? @message)
               :on-close #(when (= "timeout" %2) (reset! message nil))
               :autoHideDuration 3000}
     (when (not (nil? @message))
       [alert {:severity (cond success "success" fail "error")}
        (cond success  "Успешно"
              fail "Не удалось")])]))


;; Patients List
(defn delete-handler [id fullname]
  (let [confirm? (js/confirm (str "Удалить пациента с id " id " и именем " fullname " ?"))]
    (when (= confirm? true) (delete-patient! id))))

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
  (fn []
    (let [f (r/atom (:patient @data))
          initial-state (atom (:patient @data))
          id (:id @f)]
      (fn []
        [:div
         (cond @redirect-to-list? (do (accountant/navigate! (path-for :index)) (reset! redirect-to-list? nil)))

         [:div.patients-form
          [form-item "ФИО" [:input {:type :text :value (:fullname @f) :size 100
                                    :on-change #(swap! f assoc :fullname (-> % .-target .-value))}]]
          [form-item "Пол" [:select {:type :text :value (:gender @f)
                                     :on-change #(swap! f assoc :gender (js/parseInt (-> % .-target .-value)))}
                            [:option {:value -1} ""]
                            [:option {:value 1} "М"]
                            [:option {:value 0} "Ж"]]]

          [form-item "Дата рождения" [:input {:type :date :value (:birth_date @f) :size 100
                                              :on-change #(swap! f assoc :birth_date (-> % .-target .-value))}]]


          [form-item "Адрес"  [:input {:type :text :value (:address @f) :size 100
                                       :on-change #(swap! f assoc :address (-> % .-target .-value))}]]


          [form-item "Номер полиса ОМС" [:input {:type :number :value (:policy_number @f)
                                                 :required true
                                                 :on-change (fn [e] (let [val (-> e .-target .-value)]
                                                                      (print val)
                                                                      (when (>= 16 (count val))
                                                                        (swap! f assoc :policy_number (js/parseInt val)))))}]]

          (if (nil? id)
            [:button.btn.btn-primary
             {:on-click #(create-patient! @f) :disabled (= @initial-state @f)}
             "Сохранить"]

            [:button.btn.btn-success
             {:on-click #(edit-patient! id (dissoc @f :id)) :disabled (= @initial-state @f)}
             "Редактировать"])

          (when (not (= @initial-state @f)) [:button.btn.btn-default {:on-click #(reset! f @initial-state)
                                                                      :style {:margin-left "15px"}}
                                             "Сбросить"])]]))))


