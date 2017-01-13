#include<string>
#include<iostream>  
#include<fstream>  
#include <map>
#include <vector>
using namespace std;

class	JsonQuery {
public:
	JsonQuery(string s);
	~JsonQuery();
	JsonQuery m(string key);
	JsonQuery l(int index);
	string ms(string key);
	string ls(int index);
private:
	map<string, string> _m;
	vector<string> _v;
	string val;
	int _indexOf(string json, int start);
	int _indexOf(string json, int start, char separate);
	void clean(string& ele);
	void toM(string);
	void toL(string);
};

JsonQuery::JsonQuery(string s) :val(s)
{
	clean(s);
	if (s.length() > 0) {
		char c = s.at(0);
		if (c == '{') {
			 toM(s);
		}
		else if (c == '[') {
			 toL(s);
		}
	}
}

JsonQuery::~JsonQuery()
{
}
string JsonQuery::ms(string key) {
	return _m[key];
}
JsonQuery JsonQuery::m(string key) {
	JsonQuery j1(_m[key]);
	return j1;
}
string JsonQuery::ls(int index) {
	return _v[index];
}
JsonQuery JsonQuery::l(int index) {
	JsonQuery j1(_v[index]);
	return j1;
}
int JsonQuery::_indexOf(string json, int start) {
	return _indexOf(json, start, ',');
}
int JsonQuery::_indexOf(string json, int start, char separate) {
	int brace = 0;
	int bracket = 0;
	int double_quotation_marks = 0;
	for (; start < json.length() - 1; ++start) {
		char c = json.at(start);
		if (c == separate && brace == 0 && bracket == 0 && double_quotation_marks % 2 == 0) {
			break;
		}
		else if (c == '{') {
			++brace;
		}
		else if (c == '[') {
			++bracket;
		}
		else if (c == '"' && (start == 0 || json.at(start - 1) != '\\')) {
			++double_quotation_marks;
		}
		else if (c == '}') {
			--brace;
		}
		else if (c == ']') {
			--bracket;
		}
	}
	return start;
}
void JsonQuery::clean(string& ele) {
	int len = ele.length();
	int begin_index = 0, end_index = 0;
	for (int i = 0; i < len; ++i) {
		char c = ele.at(i);
		if ((c > 32 && c != 34) || c < 0) {
			begin_index = i;
			break;
		}
	}
	for (int i = len - 1; i >= 0; --i) {
		char c = ele.at(i);
		if ((c > 32 && c != 34) || c < 0) {
			end_index = i;
			break;
		}
	}
	if (begin_index != 0 || end_index != len - 1) {
		ele = ele.substr(begin_index, end_index - begin_index + 1);
	}
}
void JsonQuery::toM(string json) {
	for (int start = 1; start < json.length();) {
		int end = _indexOf(json, start);
		string entry = json.substr(start, end - start);
		int n = _indexOf(entry, 0, ':');
		string key = entry.substr(1, n - 2);
		clean(key);
		string val = entry.substr(n + 1);
		clean(val);
		_m[key] = val;
		start = end + 1;
	}
}
void JsonQuery::toL(string json) {
	for (int start = 1; start < json.length();) {
		int end = _indexOf(json, start);
		string ele = json.substr(start, end - start);
		clean(ele);
		_v.push_back(ele);
		start = end + 1;
	}
}

int main(){
	/*
	string s = "{\"a\":\"中a丠\"}";
	JsonQuery j(s);
	cout << j.ms("a") << endl;
	*/
	ifstream fin("c:/b.txt");
	char aa[1024];
	while (fin.getline(aa, 1024)){
		string ss(aa);
		JsonQuery jj(ss);
		cout << jj.ms("b") << endl;
	}
	system("pause");
    return 0;
}