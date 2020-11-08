criar role on postgres
name: agendasus
password: agendasus@dev

criar database no postgres:
name: agendasus-dev-core
owner: agendasus

baixar redis e rodar
make redis
cd src
./redis-server