# plagdetection

Plagchain is a project developed as a part of Master thesis of Jagrut Kosti at University of Konstanz.

plagdetection is plagiarism detection(PD) module that implements min-hash technique to identify local similarity in academic documents.
This module creates min-hashes whenever a user chooses to use this PD algorithm for document timestamps and PD.

This module runs its own server and has its own MongoDB where it organizes the hashes from Plagchain (custom blockchain used for this project) for faster PD.
  