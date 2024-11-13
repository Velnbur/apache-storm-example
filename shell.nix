{ pkgs ? import <nixpkgs> {
  config.permittedInsecurePackages = [
    "python-2.7.18.6"
  ];
} }:
with pkgs;
mkShell {
  buildInputs = [
    storm
    # clojure
    jdk22
    maven
  ];
}
