package com.example.elearning;

public class videoitem {
      private String documentId; // Document ID from Firestore
        private String thumbnailUrl;
        private String subject;
        private String title;


        public videoitem(String documentId, String thumbnailUrl, String subject, String title) {
            this.documentId = documentId;
            this.thumbnailUrl = thumbnailUrl;
            this.subject = subject;
            this.title = title;
        }

        // Getter for documentId
        public String getDocumentId() {
            return documentId;
        }

        public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getSubject() {
        return subject;
    }

    public String getTitle() {
        return title;
    }


}