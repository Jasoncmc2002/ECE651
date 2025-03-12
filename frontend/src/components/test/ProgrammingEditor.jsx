import React, { Component } from 'react';
import ContendCard from '../contents/ContentCard';
import CodeMirror from '@uiw/react-codemirror';
import { python } from '@codemirror/lang-python';
import $ from 'jquery';
import { connect } from 'react-redux';
import BACKEND_ADDRESS_URL from "../config/BackendAddressURLConfig";

class ProgrammingEditor extends Component {
    state = {
        code: '',
        error_message: '',
        res_message: '',
        time_limit: 400,  // Unit: ms

        test_input: '',
        test_output: '',
        show_submit: false,

        is_loading: false
    }

    handleTest = () => {
        // console.log('test');
        // console.log(this.state.code);
        // console.log(this.state.test_input);
        // console.log(this.state.time_limit);
        this.setState({
            error_message: '',
            test_output: '',
            is_loading: true
        });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/judge/special_judge/",
            type: "POST",
            data: {
                code: this.state.code,
                testInput: this.state.test_input,
                timeLimit: this.state.time_limit,
            },
            headers: {
                Authorization: "Bearer " + this.props.token
            },
            success: (resp) => {
                // console.log(resp);
                if (resp.error_message === 'success') {
                    this.setState({
                        test_output: resp.test_output,
                        is_loading: false
                    });
                }
                else {
                    this.setState({
                        error_message: resp.error_message,
                        is_loading: false
                    });
                }
            },
            error: (resp) => {
                this.setState({
                    error_message: "Error connecting to server, please check console",
                    is_loading: false
                })
            }
        });
    }

    // handleSubmit = () => {
    //     console.log('submit');
    //     console.log(this.state.code);
    //     this.setState({
    //         res_message: '',
    //         show_submit: false
    //     });
    //     this.setState({
    //         res_message: 'Accepted',
    //         show_submit: true
    //     });
    // }

    // renderSubmit = () => {
    //     if (this.state.show_submit) {
    //         return (
    //             <React.Fragment>
    //                 <hr />
    //                 <div className="container">
    //                     <h5>代码提交状态： {this.state.res_message}</h5>
    //                 </div>
    //             </React.Fragment>
    //         );
    //     }
    // }

    renderLoading = () => {
        if (this.state.is_loading) {
            return (
                <div className="spinner-border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            );
        }
    }

    render() {
        // calculate rows
        let rows = 1;
        if (this.state.test_input === "") {
            rows = 1;
        } else if (this.state.test_input) {
            // rows = 0;
            for (let i = 0; i < this.state.test_input.length; i++) {
                if (this.state.test_input[i] === '\n') {
                    rows += 1;
                }
            }
        }

        return (
            <div className="container">
                <ContendCard>
                    <h4 className='text-center'>Test Case Checking Tool</h4>
                    <hr />
                    <CodeMirror
                        value={this.state.code}
                        height="45vh"
                        extensions={[python()]}
                        onChange={(code) => { this.setState({ code: code }) }}
                        basicSetup={{
                            tabSize: 4
                        }}
                    />
                    <div className="row mt-3">
                        <div className="col-md-8 d-flex align-items-center">
                            <div className="row align-items-center">
                                <div className="col-auto">
                                    {this.renderLoading()}
                                </div>
                                <div className='col' style={{ color: "red" }}>
                                    {this.state.error_message}
                                </div>
                            </div>
                        </div>
                        <div className="col-md-4">
                            <div className="row">
                                <div className="col">
                                    <div className="row">
                                        <div className="col-auto">
                                            <label htmlFor="time_limit" className="col-form-label">Time Limit</label>
                                        </div>
                                        <div className="col">
                                            <div className="input-group">
                                                <input type="number" className="form-control" id="time_limit" defaultValue={this.state.time_limit} onChange={(e) => { this.setState({ time_limit: e.target.value, p_change: true }); }} min={150} max={2000} disabled={this.state.is_loading || this.state.disable_edit} />
                                                <span className="input-group-text">ms</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-auto">
                                    <button type="button" className="btn btn-outline-secondary" onClick={() => this.handleTest()} disabled={this.state.is_loading || this.state.code.length === 0}>Run Test</button>
                                </div>
                            </div>
                            {/* <div className=" float-end">
                                <button type="button" className="btn btn-outline-secondary" onClick={() => this.handleTest()} disabled={this.state.is_loading}>Run Test</button>
                                <button type="button" className="btn btn-success ms-md-3" onClick={() => this.handleSubmit()} disabled={this.state.is_loading}>Submit</button>
                            </div> */}
                        </div>
                    </div>

                    {/* {this.renderSubmit()} */}

                    <div className="accordion mt-2" id="testProgramming">
                        <div className="accordion-item">
                            <h2 className="accordion-header">
                                <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                    Test Case
                                </button>
                            </h2>
                            <div id="collapseOne" className="accordion-collapse collapse show" data-bs-parent="#testProgramming">
                                <div className="accordion-body">
                                    <label htmlFor="test_input">Input</label>
                                    <br />
                                    <textarea className='form-control mt-2' rows={rows} id='test_input' style={{ fontFamily: "monospace", resize: "auto none", textWrap: "nowrap" }} onChange={(e) => { this.setState({ test_input: e.target.value }) }} />
                                    <div className="mt-2">
                                        <span>Output</span>
                                        <div className="card card-body mt-2" style={{ padding: "6px 12px" }}>
                                            <pre className='mb-0' style={{ minHeight: "24px", fontSize: "16px" }}>{this.state.test_output + "\n"}</pre>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </ContendCard>
            </div>
        );
    }
}

const mapStateToProps = (state, props) => {
    return {
        ...props,
        user_id: state.user_id,
        username: state.username,
        name: state.name,
        permission: state.permission,
        token: state.token,
        is_login: state.is_login,
    };
};

export default connect(mapStateToProps, null)(ProgrammingEditor);